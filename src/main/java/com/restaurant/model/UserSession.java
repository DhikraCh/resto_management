package com.restaurant.model;

/**
 * PATRON SINGLETON - Session utilisateur
 * Garde en mémoire l'utilisateur actuellement connecté
 */
public class UserSession {
    private static UserSession instance;

    private String currentAdminEmail;
    private boolean isAdmin;

    private UserSession() {
        this.currentAdminEmail = null;
        this.isAdmin = false;
    }

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    // Connexion admin
    public void loginAsAdmin(String email) {
        this.currentAdminEmail = email;
        this.isAdmin = true;
        System.out.println("📝 Session admin démarrée: " + email);
    }

    // Connexion invité
    public void loginAsGuest() {
        this.currentAdminEmail = null;
        this.isAdmin = false;
        System.out.println("📝 Session invité démarrée");
    }

    // Déconnexion
    public void logout() {
        System.out.println("👋 Déconnexion: " + (isAdmin ? currentAdminEmail : "Invité"));
        this.currentAdminEmail = null;
        this.isAdmin = false;
    }

    // Getters
    public boolean isAdmin() {
        return isAdmin;
    }

    public String getCurrentAdminEmail() {
        return currentAdminEmail;
    }

    public boolean isLoggedIn() {
        return isAdmin || currentAdminEmail == null;
    }
}