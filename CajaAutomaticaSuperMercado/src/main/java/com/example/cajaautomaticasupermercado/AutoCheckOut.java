package com.example.cajaautomaticasupermercado;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Optional;

public class AutoCheckOut extends Application {

    private ListView<String> cartListView;
    private TextField barcodeTextField;
    private TextField quantityTextField;
    private Label totalLabel;
    private double total = 0.0;

    private HashMap<String, Product> productDatabase;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Caja Rápida - Tienda Departamental");

        // Simulación de la base de datos de productos
        productDatabase = new HashMap<>();
        productDatabase.put("123456", new Product("Manzana", 0.5));
        productDatabase.put("789012", new Product("Leche", 1.2));
        productDatabase.put("345678", new Product("Pan", 0.8));

        // Layout principal
        BorderPane layout = new BorderPane();

        // Lista de productos en el carrito
        cartListView = new ListView<>();
        layout.setCenter(cartListView);

        // Área de entrada para nuevos productos
        VBox inputArea = new VBox(10);
        inputArea.setPadding(new Insets(10));

        barcodeTextField = new TextField();
        barcodeTextField.setPromptText("Código de Barras");

        quantityTextField = new TextField();
        quantityTextField.setPromptText("Cantidad");

        Button addButton = new Button("Agregar");
        addButton.setOnAction(e -> addProduct());

        inputArea.getChildren().addAll(new Label("Nuevo Producto:"), barcodeTextField, quantityTextField, addButton);

        layout.setTop(inputArea);

        // Área de total, eliminación y pago
        VBox bottomArea = new VBox(10);
        bottomArea.setPadding(new Insets(10));

        HBox totalArea = new HBox(10);
        totalLabel = new Label("Total: $0.00");

        Button removeButton = new Button("Eliminar Producto Seleccionado");
        removeButton.setOnAction(e -> removeProduct());

        Button payButton = new Button("Pagar");
        payButton.setOnAction(e -> selectPaymentMethod());

        totalArea.getChildren().addAll(totalLabel, removeButton, payButton);
        bottomArea.getChildren().add(totalArea);

        // Opciones de pago
        HBox paymentOptions = new HBox(10);
        paymentOptions.setPadding(new Insets(10));

        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton creditCardOption = new RadioButton("Tarjeta de Crédito");
        creditCardOption.setToggleGroup(paymentGroup);
        RadioButton debitCardOption = new RadioButton("Tarjeta de Débito");
        debitCardOption.setToggleGroup(paymentGroup);
        RadioButton cashOption = new RadioButton("Efectivo");
        cashOption.setToggleGroup(paymentGroup);

        paymentOptions.getChildren().addAll(new Label("Método de Pago:"), creditCardOption, debitCardOption, cashOption);
        bottomArea.getChildren().add(paymentOptions);

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

        if (!productDatabase.containsKey(barcode)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Producto no encontrado.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            Product product = productDatabase.get(barcode);
            double price = product.getPrice() * quantity;
            cartListView.getItems().add(product.getName() + " - Cantidad: " + quantity + " - $" + String.format("%.2f", price));
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

    private void selectPaymentMethod() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Método de Pago");
        alert.setHeaderText("Seleccione su método de pago");
        alert.setContentText("Elija una opción:");

        ButtonType buttonTypeCredit = new ButtonType("Tarjeta de Crédito");
        ButtonType buttonTypeDebit = new ButtonType("Tarjeta de Débito");
        ButtonType buttonTypeCash = new ButtonType("Efectivo");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeCredit, buttonTypeDebit, buttonTypeCash, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeCredit) {
            handleCardPayment("crédito");
        } else if (result.get() == buttonTypeDebit) {
            handleCardPayment("débito");
        } else if (result.get() == buttonTypeCash) {
            handleCashPayment();
        }
    }

    private void handleCardPayment(String cardType) {
        // Ventana para simular la inserción de la tarjeta y validación del PIN
        Stage cardStage = new Stage();
        cardStage.setTitle("Pago con Tarjeta de " + cardType);

        VBox cardLayout = new VBox(10);
        cardLayout.setPadding(new Insets(10));

        Label insertCardLabel = new Label("Por favor, inserte su tarjeta de " + cardType);
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Número de Tarjeta");

        Button insertButton = new Button("Insertar Tarjeta");
        insertButton.setOnAction(e -> {
            if (cardNumberField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Por favor, ingrese el número de tarjeta.");
            } else {
                showPinDialog(cardStage, cardType);
            }
        });

        cardLayout.getChildren().addAll(insertCardLabel, cardNumberField, insertButton);
        Scene cardScene = new Scene(cardLayout, 300, 200);
        cardStage.setScene(cardScene);
        cardStage.show();
    }

    private void showPinDialog(Stage cardStage, String cardType) {
        // Ventana para simular la entrada del PIN
        Stage pinStage = new Stage();
        pinStage.setTitle("Validación de PIN");

        VBox pinLayout = new VBox(10);
        pinLayout.setPadding(new Insets(10));

        Label enterPinLabel = new Label("Ingrese su PIN:");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("PIN");

        Button validateButton = new Button("Validar PIN");
        validateButton.setOnAction(e -> {
            if (pinField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Por favor, ingrese el PIN.");
            } else {
                // Simulamos la validación del PIN y del saldo con el banco
                boolean isValid = validateCardTransaction(cardType);
                if (isValid) {
                    showAlert(Alert.AlertType.INFORMATION, "Transacción Exitosa", "La transacción fue exitosa. Puede retirar su tarjeta.");
                    cardStage.close();
                    pinStage.close();
                    generateReceipt();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Transacción Fallida", "La transacción falló. Por favor, intente nuevamente.");
                }
            }
        });

        pinLayout.getChildren().addAll(enterPinLabel, pinField, validateButton);
        Scene pinScene = new Scene(pinLayout, 300, 200);
        pinStage.setScene(pinScene);
        pinStage.show();
    }

    private boolean validateCardTransaction(String cardType) {
        // Aquí simulamos la validación con el banco. En la realidad, este proceso sería más complejo e involucraría una conexión a un sistema bancario.
        // Simularemos que la transacción siempre es válida.
        return true;
    }

    private void handleCashPayment() {
        // Ventana para simular el ingreso de efectivo
        Stage cashStage = new Stage();
        cashStage.setTitle("Pago en Efectivo");

        VBox cashLayout = new VBox(10);
        cashLayout.setPadding(new Insets(10));

        Label insertCashLabel = new Label("Ingrese la cantidad de efectivo:");

        TextField cashField = new TextField();
        cashField.setPromptText("Monto en efectivo");

        Button insertCashButton = new Button("Insertar Efectivo");
        insertCashButton.setOnAction(e -> {
            if (cashField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Por favor, ingrese el monto de efectivo.");
            } else {
                try {
                    double cashAmount = Double.parseDouble(cashField.getText());
                    if (cashAmount >= total) {
                        double change = cashAmount - total;
                        showAlert(Alert.AlertType.INFORMATION, "Pago Exitoso", "Pago realizado con éxito. Su cambio es: $" + String.format("%.2f", change));
                        cashStage.close();
                        generateReceipt();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "El monto ingresado es insuficiente.");
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "El monto ingresado debe ser un número válido.");
                }
            }
        });

        cashLayout.getChildren().addAll(insertCashLabel, cashField, insertCashButton);
        Scene cashScene = new Scene(cashLayout, 300, 200);
        cashStage.setScene(cashScene);
        cashStage.show();
    }

    private void generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Ticket de Compra:\n");
        for (String item : cartListView.getItems()) {
            receipt.append(item).append("\n");
        }
        receipt.append("Total: $").append(String.format("%.2f", total));

        showAlert(Alert.AlertType.INFORMATION, "Ticket de Compra", receipt.toString());
        cartListView.getItems().clear();
        total = 0.0;
        updateTotal();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clase interna para representar un producto
    private static class Product {
        private final String name;
        private final double price;

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }
}
