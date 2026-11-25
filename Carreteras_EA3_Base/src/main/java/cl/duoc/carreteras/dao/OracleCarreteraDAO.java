
package cl.duoc.carreteras.dao;
import cl.duoc.carreteras.conexion.ConexionBD;
import cl.duoc.carreteras.model.Carretera;
import cl.duoc.carreteras.model.CarreteraConcesionada;
import cl.duoc.carreteras.model.CarreteraEstatal;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class OracleCarreteraDAO implements CarreteraDAO {


    @Override
    public List<Carretera> obtenerTodos() throws PersistenciaException {

        String sql = "SELECT id, id_tipo, codigo, numero_pistas, "
                + "longitud_km, tarifa_peaje, iluminada, "
                + "tag_habilitado, altitud_maxima "
                + "FROM carretera ORDER BY id";

        List<Carretera> carreteras = new ArrayList<>();

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()
        ) {

            while (resultado.next()) {

                int id = resultado.getInt("id");
                int tipo = resultado.getInt("id_tipo");

                String codigo = resultado.getString("codigo");
                int pistas = resultado.getInt("numero_pistas");
                double longitud = resultado.getDouble("longitud_km");

                Carretera carretera;

                if (tipo == 1) {

                    double tarifa = resultado.getDouble("tarifa_peaje");
                    boolean iluminada = resultado.getInt("iluminada") == 1;

                    CarreteraConcesionada concesionada =
                            new CarreteraConcesionada(
                                    codigo, pistas, longitud, tarifa, iluminada
                            );

                    boolean tag = resultado.getInt("tag_habilitado") == 1;
                    concesionada.setTagHabilitado(tag);

                    carretera = concesionada;

                } else if (tipo == 2) {

                    double altitud = resultado.getDouble("altitud_maxima");

                    carretera = new CarreteraEstatal(
                            codigo, pistas, longitud, altitud
                    );

                } else {
                    throw new SQLException("Tipo de carretera desconocido: " + tipo);
                }

                carretera.setId(id);
                carreteras.add(carretera);
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Error al obtener las carreteras: " + e.getMessage(), e
            );
        }

        return carreteras;
    }


    @Override
    public void insertar(Carretera carretera) throws PersistenciaException {

        String sql = "INSERT INTO carretera (id_tipo, codigo, "
                + "numero_pistas, longitud_km, tarifa_peaje, "
                + "iluminada, tag_habilitado, altitud_maxima) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)
        ) {

            // Datos comunes
            sentencia.setString(2, carretera.getCodigo());
            sentencia.setInt(3, carretera.getNumeroPistas());
            sentencia.setDouble(4, carretera.getLongitudKm());

            if (carretera instanceof CarreteraConcesionada) {

                CarreteraConcesionada c = (CarreteraConcesionada) carretera;

                sentencia.setInt(1, 1);
                sentencia.setDouble(5, c.getTarifaPeaje());
                sentencia.setInt(6, c.isIluminada() ? 1 : 0);
                sentencia.setInt(7, c.isTagHabilitado() ? 1 : 0);
                sentencia.setNull(8, Types.NUMERIC);

            } else if (carretera instanceof CarreteraEstatal) {

                CarreteraEstatal e = (CarreteraEstatal) carretera;

                sentencia.setInt(1, 2);
                sentencia.setNull(5, Types.NUMERIC);
                sentencia.setNull(6, Types.NUMERIC);
                sentencia.setNull(7, Types.NUMERIC);
                sentencia.setDouble(8, e.getAltitudMaxima());

            } else {

                throw new PersistenciaException(
                        "Tipo de carretera no reconocido."
                );
            }

            sentencia.executeUpdate();

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Error al insertar la carretera: " + e.getMessage(), e
            );
        }
    }


    @Override
    public void actualizar(Carretera carretera) throws PersistenciaException {

        String sql = "UPDATE carretera SET id_tipo = ?, codigo = ?, "
                + "numero_pistas = ?, longitud_km = ?, tarifa_peaje = ?, "
                + "iluminada = ?, tag_habilitado = ?, altitud_maxima = ? "
                + "WHERE id = ?";

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)
        ) {

            // Datos comunes
            sentencia.setString(2, carretera.getCodigo());
            sentencia.setInt(3, carretera.getNumeroPistas());
            sentencia.setDouble(4, carretera.getLongitudKm());

            if (carretera instanceof CarreteraConcesionada) {

                CarreteraConcesionada c = (CarreteraConcesionada) carretera;

                sentencia.setInt(1, 1);
                sentencia.setDouble(5, c.getTarifaPeaje());
                sentencia.setInt(6, c.isIluminada() ? 1 : 0);
                sentencia.setInt(7, c.isTagHabilitado() ? 1 : 0);
                sentencia.setNull(8, Types.NUMERIC);

            } else if (carretera instanceof CarreteraEstatal) {

                CarreteraEstatal e = (CarreteraEstatal) carretera;

                sentencia.setInt(1, 2);
                sentencia.setNull(5, Types.NUMERIC);
                sentencia.setNull(6, Types.NUMERIC);
                sentencia.setNull(7, Types.NUMERIC);
                sentencia.setDouble(8, e.getAltitudMaxima());

            } else {

                throw new PersistenciaException(
                        "Tipo de carretera no reconocido."
                );
            }

            // Identificador del registro que queremos modificar
            sentencia.setInt(9, carretera.getId());

            int filasActualizadas = sentencia.executeUpdate();

            if (filasActualizadas == 0) {
                throw new PersistenciaException(
                        "No se encontró la carretera con ID: " + carretera.getId()
                );
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Error al actualizar la carretera: " + e.getMessage(), e
            );
        }
    }



    @Override
    public void eliminar(int id) throws PersistenciaException {

        String sql = "DELETE FROM carretera WHERE id = ?";

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)
        ) {

            sentencia.setInt(1, id);

            int filasEliminadas = sentencia.executeUpdate();

            if (filasEliminadas == 0) {
                throw new PersistenciaException(
                        "No se encontró la carretera con ID: " + id
                );
            }

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Error al eliminar la carretera: " + e.getMessage(), e
            );
        }
    }


}
