package cl.duoc.carreteras.dao;

import cl.duoc.carreteras.model.Carretera;

import java.util.List;

/**
 * Operaciones de almacenamiento que utiliza la ventana.
 * <p>
 * Si una operación no puede completarse, debe lanzar PersistenciaException con un
 * mensaje que indique qué operación falló; la ventana muestra ese mensaje en el
 * área de mensajes.
 */
public interface CarreteraDAO {

    /**
     * @return todas las carreteras almacenadas, cada una como objeto de su tipo
     *         (CarreteraConcesionada o CarreteraEstatal), con todos sus datos y su identificador.
     */
    List<Carretera> obtenerTodos() throws PersistenciaException;

    /**
     * Guarda una carretera nueva con todos sus datos, incluidos los propios de su tipo.
     * El identificador lo genera la base de datos.
     */
    void insertar(Carretera carretera) throws PersistenciaException;

    /**
     * Modifica todos los datos de la carretera que tiene el mismo identificador,
     * incluidos los propios de su tipo.
     */
    void actualizar(Carretera carretera) throws PersistenciaException;

    /**
     * Elimina la carretera con el identificador indicado.
     */
    void eliminar(int id) throws PersistenciaException;
}
