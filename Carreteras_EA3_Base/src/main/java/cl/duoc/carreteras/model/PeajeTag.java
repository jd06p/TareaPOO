package cl.duoc.carreteras.model;

/**
 * Comportamiento asociado a: cobro de peaje mediante TAG (telepeaje).
 */
public interface PeajeTag {

    boolean isTagHabilitado();

    void setTagHabilitado(boolean tagHabilitado);
}
