package com.restaurant.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * PATRON SINGLETON - Gestion des administrateurs
 * Gère l'enregistrement et la connexion des admins
 */
public class AdminManager {
    private static AdminManager instance;
    private static final String ADMIN_FILE = "admins.txt";
    private Map<String, String> admins;

    private AdminManager() {
        admins = new HashMap<>();
        loadAdmins();
    }

    public static AdminManager getInstance() {
        if (instance == null) {
            synchronized (AdminManager.class) {
                if (instance == null) {
                    instance = new AdminManager();
                }
            }
        }
        return instance;
    }

    // Charger les admins depuis le fichier
    private void loadAdmins() {
        File file = new File(ADMIN_FILE);
        if (!file.exists()) {
            System.out.println("Fichier admins.txt n'existe pas encore. Il sera créé lors du premier enregistrement.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    admins.put(parts[0], parts[1]);
                }
            }
            System.out.println("✅ " + admins.size() + " admin(s) chargé(s)");
        } catch (IOException e) {
            System.out.println("❌ Erreur lors du chargement des admins: " + e.getMessage());
        }
    }

    // Sauvegarder les admins dans le fichier
    private void saveAdmins() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE))) {
            for (Map.Entry<String, String> entry : admins.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
            System.out.println("✅ Admins sauvegardés dans " + ADMIN_FILE);
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    // Enregistrer un nouvel admin
    public boolean registerAdmin(String email, String password) {
        if (admins.containsKey(email)) {
            System.out.println("❌ Cet email existe déjà");
            return false;
        }

        admins.put(email, password);
        saveAdmins();
        System.out.println("✅ Admin enregistré: " + email);
        return true;
    }

    // Vérifier les identifiants
    public boolean login(String email, String password) {
        if (!admins.containsKey(email)) {
            System.out.println("❌ Email introuvable");
            return false;
        }

        if (admins.get(email).equals(password)) {
            System.out.println("✅ Connexion réussie: " + email);
            return true;
        }

        System.out.println("❌ Mot de passe incorrect");
        return false;
    }

    // Vérifier si un email existe
    public boolean emailExists(String email) {
        return admins.containsKey(email);
    }

    // Valider le format email (uniquement @gmail.com)
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$");
    }
}