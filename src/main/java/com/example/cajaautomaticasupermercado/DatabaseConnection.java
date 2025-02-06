package com.example.cajaautomaticasupermercado;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/supermercado"; // Asegúrate de que la base de datos existe
    private static final String USER = "admin"; // Reemplaza con tu usuario de MySQL
    private static final String PASSWORD = ""; // Reemplaza con tu contraseña de MySQL

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Verifica que esta línea está presente
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el driver de MySQL", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión a la base de datos", e);
        }
    }
}
