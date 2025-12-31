package com.restaurant.model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestion des notifications de livraison pour les admins
 */
public class DeliveryNotificationManager {
    private static DeliveryNotificationManager instance;
    private static final String NOTIFICATIONS_FILE = "delivery_notifications.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DeliveryNotificationManager() {
    }

    public static DeliveryNotificationManager getInstance() {
        if (instance == null) {
            synchronized (DeliveryNotificationManager.class) {
                if (instance == null) {
                    instance = new DeliveryNotificationManager();
                }
            }
        }
        return instance;
    }

    /**
     * Enregistrer une notification de livraison
     */
    public void addNotification(String livreurEmail, int orderId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOTIFICATIONS_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            writer.write(timestamp + "|" + livreurEmail + "|" + orderId);
            writer.newLine();
            System.out.println("🔔 Notification sauvegardée: " + livreurEmail + " a livré #" + orderId);
        } catch (IOException e) {
            System.out.println("❌ Erreur sauvegarde notification: " + e.getMessage());
        }
    }

    /**
     * Charger toutes les notifications
     */
    public List<String> getNotifications() {
        List<String> notifications = new ArrayList<>();
        File file = new File(NOTIFICATIONS_FILE);

        if (!file.exists()) {
            return notifications;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String timestamp = parts[0];
                    String livreur = parts[1];
                    String orderId = parts[2];
                    notifications.add("🚚 [" + timestamp + "] " + livreur + " a livré la commande #" + orderId);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Erreur lecture notifications: " + e.getMessage());
        }

        return notifications;
    }

    /**
     * Effacer toutes les notifications
     */
    public void clearNotifications() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOTIFICATIONS_FILE))) {
            writer.write("");
            System.out.println("✅ Notifications effacées");
        } catch (IOException e) {
            System.out.println("❌ Erreur effacement notifications: " + e.getMessage());
        }
    }
}