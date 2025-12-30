package com.restaurant.model;

import com.restaurant.model.order.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRON SINGLETON - Session utilisateur avec support multi-rôles
 */
public class UserSession {
    private static UserSession instance;

    private User currentUser;
    private List<Order> userOrderHistory; // Historique pour les clients connectés

    private UserSession() {
        this.currentUser = null;
        this.userOrderHistory = new ArrayList<>();
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

    // Connexion avec User
    public void login(User user) {
        this.currentUser = user;
        this.userOrderHistory = new ArrayList<>();
        System.out.println("🔐 Session démarrée: " + user.getEmail() + " (" + user.getRoleDisplay() + ")");
    }

    // Connexion invité (comme avant)
    public void loginAsGuest() {
        this.currentUser = null;
        this.userOrderHistory = new ArrayList<>();
        System.out.println("🔐 Session invité démarrée");
    }

    // Connexion admin (compatibilité avec ancien code)
    @Deprecated
    public void loginAsAdmin(String email) {
        User admin = new User(email, "", User.UserRole.ADMIN, User.UserStatus.APPROVED);
        login(admin);
    }

    // Déconnexion
    public void logout() {
        if (currentUser != null) {
            System.out.println("👋 Déconnexion: " + currentUser.getEmail());
        } else {
            System.out.println("👋 Déconnexion: Invité");
        }
        this.currentUser = null;
        this.userOrderHistory.clear();
    }

    // Ajouter une commande à l'historique de l'utilisateur
    public void addOrderToHistory(Order order) {
        if (currentUser != null && currentUser.isClient()) {
            userOrderHistory.add(order);
        }
    }

    // Obtenir l'historique de l'utilisateur
    public List<Order> getUserOrderHistory() {
        return new ArrayList<>(userOrderHistory);
    }

    // Getters
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isGuest() {
        return currentUser == null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public boolean isLivreur() {
        return currentUser != null && currentUser.isLivreur();
    }

    public boolean isClient() {
        return currentUser != null && currentUser.isClient();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }

    // Compatibilité avec ancien code
    @Deprecated
    public String getCurrentAdminEmail() {
        return getCurrentEmail();
    }

    public User.UserRole getCurrentRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}