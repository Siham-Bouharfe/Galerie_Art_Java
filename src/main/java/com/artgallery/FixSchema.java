package com.artgallery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FixSchema {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=GalerieArtDB;encrypt=true;trustServerCertificate=true;";
        String user = "admin";
        String password = "adminSQL123";

        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {

            System.out.println("Connexion à la base de données réussie...");

            // Check if column exists or just try to add it
            // We'll just try to add it. If it exists, it will fail (which is fine).
            String sql = "ALTER TABLE Artworks ADD quantity INT NOT NULL DEFAULT 0;";

            System.out.println("Exécution : " + sql);
            stmt.executeUpdate(sql);

            System.out.println("Succès ! La colonne 'quantity' a été ajoutée.");

        } catch (Exception e) {
            System.out.println("Erreur (c'est peut-être normal si la colonne existe déjà) : " + e.getMessage());
        }
    }
}
