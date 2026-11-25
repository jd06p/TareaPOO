
package cl.duoc.carreteras;

import cl.duoc.carreteras.conexion.ConexionBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaConexion {

    public static void main(String[] args) {

        System.out.println("Intentando conectar con Oracle...");

        try (
                Connection conexion = ConexionBD.getConnection();
                Statement sentencia = conexion.createStatement();
                ResultSet resultado = sentencia.executeQuery("SELECT 1 FROM DUAL")
        ) {

            if (resultado.next()) {
                System.out.println("¡Conexion exitosa!");
                System.out.println("Oracle respondio: " + resultado.getInt(1));
            }

        } catch (Exception e) {

            System.out.println("Error al conectar con Oracle:");
            System.out.println(e.getMessage());

        }
    }
}
