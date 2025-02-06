package com.example.cajaautomaticasupermercado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TransactionDAO {
    public void saveTransaction(int productId, int cantidad, double total) {
        String query = "INSERT INTO transacciones (producto_id, cantidad, total, fecha) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, cantidad);
            stmt.setDouble(3, total);
            stmt.setObject(4, LocalDateTime.now());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
