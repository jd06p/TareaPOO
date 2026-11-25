package cl.duoc.carreteras.model;

/**
 * Carretera concesionada.
 * <p>
 * Costo de mantención por km: base de $18.000.000; si está iluminada, se incrementa en un 20%.
 * <p>
 * El dato tagHabilitado (interfaz PeajeTag) no se recibe en el constructor:
 * vale false hasta que se asigne con setTagHabilitado.
 */
public class CarreteraConcesionada extends Carretera implements PeajeTag {

    public static final String TIPO = "Concesionada";
    private static final double COSTO_BASE = 18000000;
    private static final double RECARGO = 0.20;

    private double tarifaPeaje;
    private boolean iluminada;
    private boolean tagHabilitado;

    public CarreteraConcesionada(String codigo, int numeroPistas, double longitudKm,
                                 double tarifaPeaje, boolean iluminada) {
        super(codigo, numeroPistas, longitudKm);
        setTarifaPeaje(tarifaPeaje);
        this.iluminada = iluminada;
    }

    /** @return tarifa de peaje para autos, en pesos. */
    public double getTarifaPeaje() {
        return tarifaPeaje;
    }

    public void setTarifaPeaje(double tarifaPeaje) {
        if (tarifaPeaje <= 0) {
            throw new IllegalArgumentException("La tarifa de peaje debe ser mayor que cero.");
        }
        this.tarifaPeaje = tarifaPeaje;
    }

    public boolean isIluminada() {
        return iluminada;
    }

    public void setIluminada(boolean iluminada) {
        this.iluminada = iluminada;
    }

    @Override
    public boolean isTagHabilitado() {
        return tagHabilitado;
    }

    @Override
    public void setTagHabilitado(boolean tagHabilitado) {
        this.tagHabilitado = tagHabilitado;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public double calcularCostoMantencion() {
        double costo = COSTO_BASE;
        if (iluminada) {
            costo += COSTO_BASE * RECARGO;
        }
        return costo;
    }
}
