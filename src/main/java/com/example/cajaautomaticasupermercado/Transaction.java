package com.example.cajaautomaticasupermercado;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private int productId;
    private int cantidad;
    private double total;
    private LocalDateTime fecha;

    public Transaction(int id, int productId, int cantidad, double total, LocalDateTime fecha) {
        this.id = id;
        this.productId = productId;
        this.cantidad = cantidad;
        this.total = total;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
