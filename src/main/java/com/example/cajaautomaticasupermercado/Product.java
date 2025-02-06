package com.example.cajaautomaticasupermercado;

public class Product {
    private int id;
    private String codigoBarras;
    private String nombre;
    private double precio;

    public Product(int id, String codigoBarras, String nombre, double precio) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }
}
