package cl.duoc.carreteras.model;

/**
 * Datos comunes de todas las carreteras.
 * <p>
 * El identificador (id) vale 0 mientras la carretera no ha sido almacenada.
 * Los métodos set rechazan los datos inválidos con una IllegalArgumentException
 * cuyo mensaje explica el motivo.
 */
public abstract class Carretera {

    private int id;
    private String codigo;
    private int numeroPistas;
    private double longitudKm;

    protected Carretera(String codigo, int numeroPistas, double longitudKm) {
        setCodigo(codigo);
        setNumeroPistas(numeroPistas);
        setLongitudKm(longitudKm);
    }

    /** @return identificador (0 si aún no ha sido almacenada). */
    public int getId() {
        return id;
    }

    /** @param id identificador asignado al almacenar la carretera (no negativo). */
    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El identificador no puede ser negativo.");
        }
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        this.codigo = codigo.trim();
    }

    /** @return número de pistas por sentido. */
    public int getNumeroPistas() {
        return numeroPistas;
    }

    public void setNumeroPistas(int numeroPistas) {
        if (numeroPistas < 1 || numeroPistas > 8) {
            throw new IllegalArgumentException("El número de pistas debe estar entre 1 y 8.");
        }
        this.numeroPistas = numeroPistas;
    }

    public double getLongitudKm() {
        return longitudKm;
    }

    public void setLongitudKm(double longitudKm) {
        if (longitudKm <= 0) {
            throw new IllegalArgumentException("La longitud debe ser mayor que cero.");
        }
        this.longitudKm = longitudKm;
    }

    /** @return tipo de la carretera: "Concesionada" o "Estatal". */
    public abstract String getTipo();

    /** @return costo anual de mantención por kilómetro, en pesos. */
    public abstract double calcularCostoMantencion();
}
