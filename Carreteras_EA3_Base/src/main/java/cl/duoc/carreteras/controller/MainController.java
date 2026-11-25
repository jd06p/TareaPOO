package cl.duoc.carreteras.controller;

import cl.duoc.carreteras.dao.CarreteraDAO;
import cl.duoc.carreteras.dao.PersistenciaException;
import cl.duoc.carreteras.model.Carretera;
import cl.duoc.carreteras.model.CarreteraConcesionada;
import cl.duoc.carreteras.model.CarreteraEstatal;
import cl.duoc.carreteras.model.PeajeTag;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Locale;

/**
 * Controlador de la ventana principal: formulario, tabla y área de mensajes.
 * Accede a los datos únicamente a través de la interfaz CarreteraDAO.
 */
public class MainController {

    // =====================================================================
    // EA3: asigna aquí tu implementación de CarreteraDAO
    //      (por ejemplo: = new MiImplementacionDAO();).
    //      No es necesario modificar ninguna otra parte de esta clase.
    // =====================================================================
    private final CarreteraDAO dao = null;

    private static final Locale LOCALE_CL = Locale.forLanguageTag("es-CL");

    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNumeroPistas;
    @FXML private TextField txtLongitudKm;
    @FXML private TextField txtTarifaPeaje;
    @FXML private CheckBox chkIluminada;
    @FXML private CheckBox chkTagHabilitado;
    @FXML private TextField txtAltitudMaxima;
    @FXML private Button btnRegistrar;
    @FXML private Button btnCancelar;

    @FXML private TableView<Carretera> tabla;
    @FXML private TableColumn<Carretera, Integer> colId;
    @FXML private TableColumn<Carretera, String> colTipo;
    @FXML private TableColumn<Carretera, String> colCodigo;
    @FXML private TableColumn<Carretera, Integer> colPistas;
    @FXML private TableColumn<Carretera, Double> colLongitud;
    @FXML private TableColumn<Carretera, String> colTag;
    @FXML private TableColumn<Carretera, String> colCosto;

    @FXML private Label lblMensaje;

    /** Carretera que se está editando; null cuando el formulario registra una nueva. */
    private Carretera enEdicion;

    @FXML
    private void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList(CarreteraConcesionada.TIPO, CarreteraEstatal.TIPO));
        cbTipo.valueProperty().addListener((obs, anterior, actual) -> actualizarCamposPorTipo());

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colPistas.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getNumeroPistas()));
        colLongitud.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getLongitudKm()));
        colTag.setCellValueFactory(c -> new SimpleStringProperty(textoTag(c.getValue())));
        colCosto.setCellValueFactory(c ->
                new SimpleStringProperty(formatearMonto(c.getValue().calcularCostoMantencion())));
        tabla.setPlaceholder(new Label("No hay carreteras para mostrar."));

        limpiarFormulario();
        cargarTabla();
    }

    // ---------------------------------------------------------------- acciones

    @FXML
    private void onRegistrar() {
        if (!hayDao()) {
            return;
        }
        Carretera carretera;
        try {
            carretera = crearDesdeFormulario();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return;
        }

        try {
            if (enEdicion == null) {
                dao.insertar(carretera);
                cargarTabla();
                limpiarFormulario();
                mostrarExito(describir(carretera) + " registrada correctamente.");
            } else {
                carretera.setId(enEdicion.getId());
                dao.actualizar(carretera);
                cargarTabla();
                limpiarFormulario();
                mostrarExito(describir(carretera) + " actualizada correctamente.");
            }
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onEditar() {
        Carretera seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una carretera de la tabla para editar.");
            return;
        }
        enEdicion = seleccionada;
        cbTipo.setValue(seleccionada.getTipo());
        cbTipo.setDisable(true);
        txtCodigo.setText(seleccionada.getCodigo());
        txtNumeroPistas.setText(String.valueOf(seleccionada.getNumeroPistas()));
        txtLongitudKm.setText(String.valueOf(seleccionada.getLongitudKm()));
        if (seleccionada instanceof CarreteraConcesionada concesionada) {
            txtTarifaPeaje.setText(String.valueOf(concesionada.getTarifaPeaje()));
            chkIluminada.setSelected(concesionada.isIluminada());
            chkTagHabilitado.setSelected(concesionada.isTagHabilitado());
        } else if (seleccionada instanceof CarreteraEstatal estatal) {
            txtAltitudMaxima.setText(String.valueOf(estatal.getAltitudMaxima()));
        }
        btnRegistrar.setText("Guardar cambios");
        btnCancelar.setDisable(false);
        txtCodigo.requestFocus();
        mostrarInfo("Editando " + describir(seleccionada)
                + ". Modifica los datos y presiona Guardar cambios.");
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        mostrarInfo("Edición cancelada.");
    }

    @FXML
    private void onEliminar() {
        if (!hayDao()) {
            return;
        }
        Carretera seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una carretera de la tabla para eliminar.");
            return;
        }
        try {
            dao.eliminar(seleccionada.getId());
            if (enEdicion != null && enEdicion.getId() == seleccionada.getId()) {
                limpiarFormulario();
            }
            cargarTabla();
            mostrarExito(describir(seleccionada) + " eliminada correctamente.");
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    // ---------------------------------------------------------------- apoyo

    /** Vuelve a leer todas las carreteras desde el almacenamiento y las muestra en la tabla. */
    private void cargarTabla() {
        if (!hayDao()) {
            tabla.getItems().clear();
            return;
        }
        try {
            tabla.setItems(FXCollections.observableArrayList(dao.obtenerTodos()));
        } catch (PersistenciaException e) {
            tabla.getItems().clear();
            mostrarError("Error al leer la información: " + e.getMessage());
        }
    }

    private boolean hayDao() {
        if (dao == null) {
            mostrarError("Aún no hay una implementación de CarreteraDAO asignada en MainController.");
            return false;
        }
        return true;
    }

    private Carretera crearDesdeFormulario() {
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona el tipo.");
        }
        String codigo = txtCodigo.getText();
        int numeroPistas = leerEntero(txtNumeroPistas.getText(), "Pistas: debe ser un número entero.");
        double longitudKm = leerDecimal(txtLongitudKm.getText(), "Longitud: debe ser un número.");

        if (CarreteraConcesionada.TIPO.equals(tipo)) {
            CarreteraConcesionada concesionada = new CarreteraConcesionada(codigo, numeroPistas, longitudKm,
                    leerDecimal(txtTarifaPeaje.getText(), "Tarifa de peaje: debe ser un número."),
                    chkIluminada.isSelected());
            concesionada.setTagHabilitado(chkTagHabilitado.isSelected());
            return concesionada;
        }
        return new CarreteraEstatal(codigo, numeroPistas, longitudKm,
                leerDecimal(txtAltitudMaxima.getText(), "Altitud máxima: debe ser un número."));
    }

    private int leerEntero(String texto, String mensajeError) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    private double leerDecimal(String texto, String mensajeError) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    private void actualizarCamposPorTipo() {
        boolean esConcesionada = CarreteraConcesionada.TIPO.equals(cbTipo.getValue());
        boolean esEstatal = CarreteraEstatal.TIPO.equals(cbTipo.getValue());
        txtTarifaPeaje.setDisable(!esConcesionada);
        chkIluminada.setDisable(!esConcesionada);
        chkTagHabilitado.setDisable(!esConcesionada);
        txtAltitudMaxima.setDisable(!esEstatal);
    }

    private void limpiarFormulario() {
        enEdicion = null;
        cbTipo.setDisable(false);
        cbTipo.setValue(null);
        txtCodigo.clear();
        txtNumeroPistas.clear();
        txtLongitudKm.clear();
        txtTarifaPeaje.clear();
        chkIluminada.setSelected(false);
        chkTagHabilitado.setSelected(false);
        txtAltitudMaxima.clear();
        btnRegistrar.setText("Registrar");
        btnCancelar.setDisable(true);
        actualizarCamposPorTipo();
        tabla.getSelectionModel().clearSelection();
    }

    private String describir(Carretera carretera) {
        return carretera.getCodigo() + " (" + carretera.getTipo() + ")";
    }

    private String textoTag(Carretera carretera) {
        if (carretera instanceof PeajeTag peaje) {
            return peaje.isTagHabilitado() ? "Sí" : "No";
        }
        return "—";
    }

    private String formatearMonto(double monto) {
        return String.format(LOCALE_CL, "$%,.0f", monto);
    }

    private void mostrarExito(String mensaje) {
        mostrarMensaje(mensaje, "#eef5ec", "#a9cfa1", "#1e4620");
    }

    private void mostrarInfo(String mensaje) {
        mostrarMensaje(mensaje, "#eef3f8", "#a8c1d9", "#1f3a56");
    }

    private void mostrarError(String mensaje) {
        mostrarMensaje(mensaje, "#fbeeee", "#e0a3a3", "#8a1f1f");
    }

    private void mostrarMensaje(String mensaje, String fondo, String borde, String texto) {
        lblMensaje.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: " + fondo
                + "; -fx-border-color: " + borde + "; -fx-text-fill: " + texto + ";");
        lblMensaje.setText(mensaje);
    }
}
