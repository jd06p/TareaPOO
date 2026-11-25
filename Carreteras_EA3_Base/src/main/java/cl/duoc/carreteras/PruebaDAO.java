
package cl.duoc.carreteras;

import cl.duoc.carreteras.dao.OracleCarreteraDAO;
import cl.duoc.carreteras.model.Carretera;
import cl.duoc.carreteras.model.CarreteraConcesionada;
import cl.duoc.carreteras.model.CarreteraEstatal;

import java.util.List;

public class PruebaDAO {

    public static void main(String[] args) {

        OracleCarreteraDAO dao = new OracleCarreteraDAO();

        // Códigos únicos para evitar conflictos al repetir la prueba.
        String codigoC = "TEST-C-" + System.currentTimeMillis();
        String codigoE = "TEST-E-" + System.currentTimeMillis();

        try {

            // 1. Crear carretera concesionada
            CarreteraConcesionada concesionada =
                    new CarreteraConcesionada(
                            codigoC, 3, 120.5, 2500, true
                    );

            concesionada.setTagHabilitado(true);

            // 2. Crear carretera estatal
            CarreteraEstatal estatal =
                    new CarreteraEstatal(
                            codigoE, 2, 85.5, 2500
                    );

            // 3. Insertar ambas carreteras
            dao.insertar(concesionada);
            dao.insertar(estatal);

            System.out.println("Carreteras insertadas correctamente.");

            // 4. Recuperar todas las carreteras
            List<Carretera> carreteras = dao.obtenerTodos();

            // 5. Mostrar las carreteras recuperadas
            for (Carretera c : carreteras) {

                if (c.getCodigo().equals(codigoC)
                        || c.getCodigo().equals(codigoE)) {

                    System.out.println("--------------------");
                    System.out.println("ID: " + c.getId());
                    System.out.println("Código: " + c.getCodigo());
                    System.out.println("Tipo: " + c.getTipo());
                    System.out.println("Pistas: " + c.getNumeroPistas());
                    System.out.println("Longitud: " + c.getLongitudKm());
                }
            }



        } catch (Exception e) {

            System.out.println("Error en la prueba DAO:");
            e.printStackTrace();
        }
    }
}
