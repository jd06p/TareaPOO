package cl.duoc.carreteras.model;

/**
 * Carretera estatal.
 * <p>
 * Costo de mantención por km: base de $9.000.000; si su altitud máxima supera los
 * 2.000 metros, se incrementa en un 30%.
 */
public class CarreteraEstatal extends Carretera {

    public static final String TIPO = "Estatal";
    private static final double COSTO_BASE = 9000000;
    private static final double RECARGO = 0.30;
    private static final double ALTITUD_RECARGO = 2000;

    private double altitudMaxima;

    public CarreteraEstatal(String codigo, int numeroPistas, double longitudKm, double altitudMaxima) {
        super(codigo, numeroPistas, longitudKm);
        setAltitudMaxima(altitudMaxima);
    }

    /** @return altitud máxima del trazado, en metros sobre el nivel del mar. */
    public double getAltitudMaxima() {
        return altitudMaxima;
    }

    public void setAltitudMaxima(double altitudMaxima) {
        if (altitudMaxima <= 0) {
            throw new IllegalArgumentException("La altitud máxima debe ser mayor que cero.");
        }
        this.altitudMaxima = altitudMaxima;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public double calcularCostoMantencion() {
        double costo = COSTO_BASE;
        if (altitudMaxima > ALTITUD_RECARGO) {
            costo += COSTO_BASE * RECARGO;
        }
        return costo;
    }
}
