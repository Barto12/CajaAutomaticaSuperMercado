package com.example.cajaautomaticasupermercado;

import javafx.scene.control.Alert;
import java.util.Random;

public class PaymentHandler {
    public boolean processCardPayment(String cardNumber, String pin) {
        if (cardNumber.length() == 16 && pin.length() == 4) {
            Random random = new Random();
            return random.nextBoolean(); // Simulación de éxito o fallo aleatorio
        }
        return false;
    }

    public double processCashPayment(double total, double cashProvided) {
        if (cashProvided >= total) {
            return cashProvided - total; // Devuelve el cambio
        } else {
            showAlert("Pago insuficiente", "El monto ingresado no es suficiente para cubrir el total.");
            return -1;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
