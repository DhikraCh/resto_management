package com.restaurant.model;

import com.restaurant.model.order.Order;
import com.restaurant.model.order.OrderItem;
import com.restaurant.model.menu.MenuIt;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRON SINGLETON - Gestion de la persistance des commandes
 * Sauvegarde et charge les commandes depuis/vers un fichier
 */
public class OrdersManager {
    private static OrdersManager instance;
    private static final String ORDERS_FILE = "orders.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private OrdersManager() {
        System.out.println("📦 OrdersManager initialisé");
    }

    public static OrdersManager getInstance() {
        if (instance == null) {
            synchronized (OrdersManager.class) {
                if (instance == null) {
                    instance = new OrdersManager();
                }
            }
        }
        return instance;
    }

    /**
     * Sauvegarder toutes les commandes dans le fichier
     */
    public void saveOrders(List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : orders) {
                if (order.isPaid()) {
                    // Format: ORDER_ID|DATE|TOTAL|PAID|PAYMENT_METHOD|STATUS|PROCESSED_TIME
                    String processedTime = order.getProcessedTime() != null ?
                            order.getProcessedTime().format(DATE_FORMATTER) : "";

                    writer.write("ORDER:" + order.getOrderId() + "|" +
                            order.getOrderTime().format(DATE_FORMATTER) + "|" +
                            order.getTotal() + "|" + order.isPaid() + "|" +
                            order.getPaymentMethod() + "|" +
                            order.getStatus().name() + "|" +
                            processedTime);
                    writer.newLine();

                    // Items: ITEM|NAME|PRICE|QUANTITY
                    for (OrderItem item : order.getItems()) {
                        writer.write("ITEM:" + item.getMenuItem().getName() + "|" +
                                item.getMenuItem().getPrice() + "|" +
                                item.getQuantity());
                        writer.newLine();
                    }

                    writer.write("---"); // Séparateur de commandes
                    writer.newLine();
                }
            }
            System.out.println("✅ " + orders.size() + " commande(s) sauvegardée(s) dans " + ORDERS_FILE);
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la sauvegarde des commandes: " + e.getMessage());
        }
    }

    /**
     * Charger toutes les commandes depuis le fichier
     */
    public List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);

        if (!file.exists()) {
            System.out.println("ℹ️ Aucun fichier de commandes trouvé. Un nouveau sera créé.");
            return orders;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Order currentOrder = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ORDER:")) {
                    // Créer nouvelle commande
                    String[] parts = line.substring(6).split("\\|");
                    currentOrder = new Order();

                    // Restaurer le paymentMethod si disponible (index 4)
                    if (parts.length > 4 && !parts[4].isEmpty()) {
                        currentOrder.setPaymentMethod(parts[4]);
                    }

                    // Restaurer le status si disponible (index 5)
                    if (parts.length > 5 && !parts[5].isEmpty()) {
                        try {
                            Order.OrderStatus status = Order.OrderStatus.valueOf(parts[5]);
                            currentOrder.setStatus(status);
                        } catch (IllegalArgumentException e) {
                            // Si le statut est invalide, garder PENDING par défaut
                        }
                    }

                    // Restaurer processedTime si disponible (index 6)
                    if (parts.length > 6 && !parts[6].isEmpty()) {
                        try {
                            LocalDateTime processedTime = LocalDateTime.parse(parts[6], DATE_FORMATTER);
                            currentOrder.setProcessedTime(processedTime);
                        } catch (Exception e) {
                            // Si erreur de parsing, ignorer
                        }
                    }

                    orders.add(currentOrder);

                } else if (line.startsWith("ITEM:") && currentOrder != null) {
                    // Ajouter item à la commande courante
                    String[] parts = line.substring(5).split("\\|");
                    String itemName = parts[0];
                    double itemPrice = Double.parseDouble(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);

                    // Recréer MenuItem
                    MenuIt menuItem = new MenuIt(itemName, "", itemPrice);
                    currentOrder.addItem(menuItem, quantity);

                } else if (line.equals("---")) {
                    // Fin de la commande courante
                    if (currentOrder != null) {
                        // Simuler le paiement pour marquer comme payé
                        currentOrder.setPaymentStrategy(new com.restaurant.model.payment.CashPaymentStrategy());
                        currentOrder.processPayment();
                    }
                    currentOrder = null;
                }
            }

            System.out.println("✅ " + orders.size() + " commande(s) chargée(s) depuis " + ORDERS_FILE);

        } catch (IOException e) {
            System.out.println("❌ Erreur lors du chargement des commandes: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Ajouter une commande au fichier (ajout incrémental)
     */
    public void appendOrder(Order order) {
        if (!order.isPaid()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE, true))) {
            // Format: ORDER_ID|DATE|TOTAL|PAID|PAYMENT_METHOD|STATUS|PROCESSED_TIME
            String processedTime = order.getProcessedTime() != null ?
                    order.getProcessedTime().format(DATE_FORMATTER) : "";

            writer.write("ORDER:" + order.getOrderId() + "|" +
                    order.getOrderTime().format(DATE_FORMATTER) + "|" +
                    order.getTotal() + "|" + order.isPaid() + "|" +
                    order.getPaymentMethod() + "|" +
                    order.getStatus().name() + "|" +
                    processedTime);
            writer.newLine();

            // Items
            for (OrderItem item : order.getItems()) {
                writer.write("ITEM:" + item.getMenuItem().getName() + "|" +
                        item.getMenuItem().getPrice() + "|" +
                        item.getQuantity());
                writer.newLine();
            }

            writer.write("---");
            writer.newLine();

            System.out.println("✅ Commande #" + order.getOrderId() + " ajoutée au fichier");

        } catch (IOException e) {
            System.out.println("❌ Erreur lors de l'ajout de la commande: " + e.getMessage());
        }
    }
}