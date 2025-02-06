package com.example.cajaautomaticasupermercado;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class AutoCheckOut extends Application {
    private ListView<String> cartListView;
    TextField barcodeTextField;
    private TextField quantityTextField;
    private Label totalLabel;
    private double total = 0.0;
    ProductDAO productDAO = new ProductDAO();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Caja Rápida - Tienda Departamental");
        BorderPane layout = new BorderPane();

        // Agregar el logo de Walmart
        Image walmartLogo = new Image("file:src/main/resources/images/walmart_logo.png");
        ImageView logoView = new ImageView(walmartLogo);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);
        layout.setTop(logoView);
        BorderPane.setMargin(logoView, new Insets(10, 10, 10, 10));

        cartListView = new ListView<>();
        layout.setCenter(cartListView);

        VBox inputArea = new VBox(10);
        inputArea.setPadding(new Insets(10));
        barcodeTextField = new TextField();
        barcodeTextField.setPromptText("Código de Barras");
        quantityTextField = new TextField();
        quantityTextField.setPromptText("Cantidad");
        Button addButton = new Button("Agregar");
        addButton.setOnAction(e -> addProduct());
        inputArea.getChildren().addAll(new Label("Nuevo Producto:"), barcodeTextField, quantityTextField, addButton);
        layout.setLeft(inputArea);

        VBox bottomArea = new VBox(10);
        bottomArea.setPadding(new Insets(10));
        HBox totalArea = new HBox(10);
        totalLabel = new Label("Total: $0.00");
        Button removeButton = new Button("Eliminar Producto Seleccionado");
        removeButton.setOnAction(e -> removeProduct());
        Button payButton = new Button("Pagar");
        payButton.setOnAction(e -> selectPaymentMethod(primaryStage));
        totalArea.getChildren().addAll(totalLabel, removeButton, payButton);
        bottomArea.getChildren().add(totalArea);
        layout.setBottom(bottomArea);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addProduct() {
        String barcode = barcodeTextField.getText();
        String quantityText = quantityTextField.getText();
        if (barcode.isEmpty() || quantityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor, ingrese el código de barras y la cantidad.");
            return;
        }
        Product product = productDAO.getProductByBarcode(barcode);
        if (product == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Producto no encontrado.");
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityText);
            double price = product.getPrecio() * quantity;
            cartListView.getItems().add(product.getNombre() + " - Cantidad: " + quantity + " - $" + String.format("%.2f", price));
            total += price;
            updateTotal();
            barcodeTextField.clear();
            quantityTextField.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "La cantidad debe ser un número válido.");
        }
    }

    private void removeProduct() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedItem = cartListView.getItems().get(selectedIndex);
            String priceText = selectedItem.substring(selectedItem.lastIndexOf('$') + 1);
            double price = Double.parseDouble(priceText);
            total -= price;
            cartListView.getItems().remove(selectedIndex);
            updateTotal();
        } else {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un producto para eliminar.");
        }
    }

    private void updateTotal() {
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void selectPaymentMethod(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Seleccionar Método de Pago");
        alert.setHeaderText("Seleccione su método de pago:");
        alert.setContentText("¿Desea pagar con tarjeta o efectivo?");
        ButtonType buttonTypeTarjeta = new ButtonType("Tarjeta");
        ButtonType buttonTypeEfectivo = new ButtonType("Efectivo");
        ButtonType buttonTypeCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeTarjeta, buttonTypeEfectivo, buttonTypeCancelar);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeTarjeta) {
                processCardPayment(primaryStage);
            } else if (result.get() == buttonTypeEfectivo) {
                processCashPayment(primaryStage);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Pago", "Operación cancelada.");
            }
        }
    }

    private void processCardPayment(Stage primaryStage) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Pago con Tarjeta");
        dialog.setHeaderText("Ingrese su NIP:");

        ButtonType loginButtonType = new ButtonType("Procesar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        PasswordField password = new PasswordField();
        password.setPromptText("NIP");

        GridPane grid = new GridPane();
        grid.add(new Label("NIP:"), 0, 0);
        grid.add(password, 1, 0);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> password.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return password.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String nip = result.get();
            if (validateNIP(nip)) {
                showReceipt(primaryStage, "Tarjeta", total);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "NIP incorrecto.");
            }
        }
    }

    private boolean validateNIP(String nip) {
        // Aquí puedes agregar la lógica para validar el NIP
        // Por ahora, asumimos que cualquier NIP de 4 dígitos es válido
        return nip.length() == 4 && nip.matches("\\d+");
    }

    private void processCashPayment(Stage primaryStage) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Pago con Efectivo");
        dialog.setHeaderText("Ingrese la cantidad en efectivo:");

        ButtonType loginButtonType = new ButtonType("Procesar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        TextField cashAmountField = new TextField();
        cashAmountField.setPromptText("Cantidad");

        GridPane grid = new GridPane();
        grid.add(new Label("Cantidad:"), 0, 0);
        grid.add(cashAmountField, 1, 0);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> cashAmountField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                try {
                    return Double.parseDouble(cashAmountField.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Cantidad inválida.");
                    return null;
                }
            }
            return null;
        });

        Optional<Double> result = dialog.showAndWait();
        if (result.isPresent()) {
            double cashAmount = result.get();
            if (cashAmount >= total) {
                double change = cashAmount - total;
                showReceipt(primaryStage, "Efectivo", total, change);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Cantidad insuficiente.");
            }
        }
    }

    private void showReceipt(Stage primaryStage, String paymentMethod, double amount) {
        Stage receiptStage = new Stage();
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        receiptStage.setTitle("Recibo");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label paymentLabel = new Label("Método de Pago: " + paymentMethod);
        Label amountLabel = new Label("Monto: $" + String.format("%.2f", amount));

        vbox.getChildren().addAll(paymentLabel, amountLabel);

        Scene scene = new Scene(vbox, 300, 150);
        receiptStage.setScene(scene);
        receiptStage.showAndWait();

        clearCart();
    }

    private void showReceipt(Stage primaryStage, String paymentMethod, double amount, double change) {
        Stage receiptStage = new Stage();
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        receiptStage.setTitle("Recibo");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label paymentLabel = new Label("Método de Pago: " + paymentMethod);
        Label amountLabel = new Label("Monto: $" + String.format("%.2f", amount));
        Label changeLabel = new Label("Cambio: $" + String.format("%.2f", change));

        vbox.getChildren().addAll(paymentLabel, amountLabel, changeLabel);

        Scene scene = new Scene(vbox, 300, 180);
        receiptStage.setScene(scene);
        receiptStage.showAndWait();

        clearCart();
    }

    private void clearCart() {
        cartListView.getItems().clear();
        total = 0.0;
        updateTotal();
    }
}