package com.restaurant.model;

/**
 * Classe représentant un utilisateur du système
 */
public class User {
    public enum UserRole {
        CLIENT,
        LIVREUR,
        ADMIN
    }

    public enum UserStatus {
        PENDING,    // En attente d'approbation
        APPROVED,   // Approuvé
        REJECTED    // Rejeté
    }

    private String email;
    private String password;
    private UserRole role;
    private UserStatus status;

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
        // Les clients sont auto-approuvés, les autres en attente
        this.status = (role == UserRole.CLIENT) ? UserStatus.APPROVED : UserStatus.PENDING;
    }

    public User(String email, String password, UserRole role, UserStatus status) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public boolean isClient() {
        return role == UserRole.CLIENT;
    }

    public boolean isLivreur() {
        return role == UserRole.LIVREUR;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isPending() {
        return status == UserStatus.PENDING;
    }

    public boolean isApproved() {
        return status == UserStatus.APPROVED;
    }

    // Setters
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getRoleDisplay() {
        switch (role) {
            case CLIENT: return "👤 Client";
            case LIVREUR: return "🚚 Livreur";
            case ADMIN: return "👨‍💼 Admin";
            default: return "❓ Inconnu";
        }
    }

    public String getStatusDisplay() {
        switch (status) {
            case PENDING: return "⏳ En attente";
            case APPROVED: return "✅ Approuvé";
            case REJECTED: return "❌ Rejeté";
            default: return "❓ Inconnu";
        }
    }

    @Override
    public String toString() {
        return email + " - " + getRoleDisplay() + " - " + getStatusDisplay();
    }
}