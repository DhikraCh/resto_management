package com.restaurant.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRON SINGLETON - Gestion des utilisateurs
 */
public class UserManager {
    private static UserManager instance;
    private static final String USERS_FILE = "users.txt";
    private List<User> users;

    private UserManager() {
        users = new ArrayList<>();
        loadUsers();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    // Charger tous les utilisateurs
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            System.out.println("📝 Fichier users.txt n'existe pas. Il sera créé.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 4) {
                    String email = parts[0];
                    String password = parts[1];
                    User.UserRole role = User.UserRole.valueOf(parts[2]);
                    User.UserStatus status = User.UserStatus.valueOf(parts[3]);
                    users.add(new User(email, password, role, status));
                }
            }
            System.out.println("✅ " + users.size() + " utilisateur(s) chargé(s)");
        } catch (IOException e) {
            System.out.println("❌ Erreur chargement users: " + e.getMessage());
        }
    }

    // Sauvegarder tous les utilisateurs
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(user.getEmail() + ":" +
                        user.getPassword() + ":" +
                        user.getRole().name() + ":" +
                        user.getStatus().name());
                writer.newLine();
            }
            System.out.println("✅ Utilisateurs sauvegardés");
        } catch (IOException e) {
            System.out.println("❌ Erreur sauvegarde: " + e.getMessage());
        }
    }

    // Enregistrer un nouvel utilisateur
    public boolean registerUser(String email, String password, User.UserRole role) {
        if (emailExists(email)) {
            System.out.println("❌ Email déjà utilisé");
            return false;
        }

        User newUser = new User(email, password, role);
        users.add(newUser);
        saveUsers();

        if (role == User.UserRole.CLIENT) {
            System.out.println("✅ Client enregistré: " + email);
        } else {
            System.out.println("⏳ Demande en attente: " + email + " (" + role + ")");
        }
        return true;
    }

    // Connexion
    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                if (user.isPending()) {
                    System.out.println("⏳ Compte en attente d'approbation");
                    return null;
                }
                if (!user.isApproved()) {
                    System.out.println("❌ Compte non approuvé");
                    return null;
                }
                System.out.println("✅ Connexion réussie: " + email);
                return user;
            }
        }
        System.out.println("❌ Identifiants incorrects");
        return null;
    }

    // Obtenir utilisateurs en attente
    public List<User> getPendingUsers() {
        List<User> pending = new ArrayList<>();
        for (User user : users) {
            if (user.isPending()) {
                pending.add(user);
            }
        }
        return pending;
    }

    // Approuver un utilisateur
    public boolean approveUser(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setStatus(User.UserStatus.APPROVED);
                saveUsers();
                System.out.println("✅ Utilisateur approuvé: " + email);
                return true;
            }
        }
        return false;
    }

    // Rejeter un utilisateur
    public boolean rejectUser(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setStatus(User.UserStatus.REJECTED);
                saveUsers();
                System.out.println("❌ Utilisateur rejeté: " + email);
                return true;
            }
        }
        return false;
    }

    // Vérifier si email existe
    public boolean emailExists(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    // Obtenir tous les utilisateurs
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Obtenir utilisateur par email
    public User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    // Validation email
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$");
    }
}