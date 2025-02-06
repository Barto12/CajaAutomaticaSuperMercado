package com.example.cajaautomaticasupermercado;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ReceiptScreen {
    public void showReceipt(List<String> items, double total) {
        Stage receiptStage = new Stage();
        receiptStage.setTitle("Ticket de Compra");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label titleLabel = new Label("Ticket de Compra");
        layout.getChildren().add(titleLabel);

        for (String item : items) {
            layout.getChildren().add(new Label(item));
        }

        Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
        layout.getChildren().add(totalLabel);

        Scene scene = new Scene(layout, 300, 400);
        receiptStage.setScene(scene);
        receiptStage.show();

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                Platform.runLater(receiptStage::close);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
