package com.restaurant.model;

import com.restaurant.model.order.Order;
import com.restaurant.model.order.OrderItem;
import com.restaurant.model.menu.MenuIt;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PATRON SINGLETON - Gestion de l'historique des commandes clients
 * Format: client_orders.txt avec association email -> commandes
 */
public class ClientOrderManager {
    private static ClientOrderManager instance;
    private static final String CLIENT_ORDERS_FILE = "client_orders.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ClientOrderManager() {
        System.out.println("📋 ClientOrderManager initialisé");
    }

    public static ClientOrderManager getInstance() {
        if (instance == null) {
            synchronized (ClientOrderManager.class) {
                if (instance == null) {
                    instance = new ClientOrderManager();
                }
            }
        }
        return instance;
    }

    /**
     * Sauvegarder une commande pour un client
     */
    public void saveClientOrder(String clientEmail, Order order) {
        if (clientEmail == null || order == null || !order.isPaid()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CLIENT_ORDERS_FILE, true))) {
            // Format: CLIENT_EMAIL|ORDER_ID|DATE|TOTAL|PAYMENT_METHOD|STATUS
            writer.write("CLIENT:" + clientEmail);
            writer.newLine();
            writer.write("ORDER:" + order.getOrderId() + "|" +
                    order.getOrderTime().format(DATE_FORMATTER) + "|" +
                    order.getTotal() + "|" +
                    order.getPaymentMethod() + "|" +
                    order.getStatus().name());
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

            System.out.println("✅ Commande sauvegardée pour " + clientEmail);

        } catch (IOException e) {
            System.out.println("❌ Erreur sauvegarde commande client: " + e.getMessage());
        }
    }

    /**
     * Charger l'historique d'un client
     */
    public List<Order> loadClientOrders(String clientEmail) {
        List<Order> orders = new ArrayList<>();
        File file = new File(CLIENT_ORDERS_FILE);

        if (!file.exists()) {
            return orders;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentClient = null;
            Order currentOrder = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("CLIENT:")) {
                    currentClient = line.substring(7);

                } else if (line.startsWith("ORDER:") && clientEmail.equals(currentClient)) {
                    String[] parts = line.substring(6).split("\\|");
                    currentOrder = new Order();

                    if (parts.length > 3) {
                        currentOrder.setPaymentMethod(parts[3]);
                    }
                    if (parts.length > 4) {
                        try {
                            currentOrder.setStatus(Order.OrderStatus.valueOf(parts[4]));
                        } catch (IllegalArgumentException e) {
                            // Ignorer
                        }
                    }

                    orders.add(currentOrder);

                } else if (line.startsWith("ITEM:") && currentOrder != null && clientEmail.equals(currentClient)) {
                    String[] parts = line.substring(5).split("\\|");
                    String itemName = parts[0];
                    double itemPrice = Double.parseDouble(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);

                    MenuIt menuItem = new MenuIt(itemName, "", itemPrice);
                    currentOrder.addItem(menuItem, quantity);

                } else if (line.equals("---")) {
                    if (currentOrder != null && clientEmail.equals(currentClient)) {
                        currentOrder.setPaymentStrategy(new com.restaurant.model.payment.CashPaymentStrategy());
                        currentOrder.processPayment();
                    }
                    currentOrder = null;
                    currentClient = null;
                }
            }

            System.out.println("✅ " + orders.size() + " commande(s) chargée(s) pour " + clientEmail);

        } catch (IOException e) {
            System.out.println("❌ Erreur chargement historique: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Obtenir tous les historiques (pour admin)
     */
    public Map<String, List<Order>> loadAllClientOrders() {
        Map<String, List<Order>> allOrders = new HashMap<>();
        File file = new File(CLIENT_ORDERS_FILE);

        if (!file.exists()) {
            return allOrders;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentClient = null;
            Order currentOrder = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("CLIENT:")) {
                    currentClient = line.substring(7);
                    if (!allOrders.containsKey(currentClient)) {
                        allOrders.put(currentClient, new ArrayList<>());
                    }

                } else if (line.startsWith("ORDER:")) {
                    String[] parts = line.substring(6).split("\\|");
                    currentOrder = new Order();

                    if (parts.length > 3) {
                        currentOrder.setPaymentMethod(parts[3]);
                    }
                    if (parts.length > 4) {
                        try {
                            currentOrder.setStatus(Order.OrderStatus.valueOf(parts[4]));
                        } catch (IllegalArgumentException e) {
                            // Ignorer
                        }
                    }

                    if (currentClient != null) {
                        allOrders.get(currentClient).add(currentOrder);
                    }

                } else if (line.startsWith("ITEM:") && currentOrder != null) {
                    String[] parts = line.substring(5).split("\\|");
                    String itemName = parts[0];
                    double itemPrice = Double.parseDouble(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);

                    MenuIt menuItem = new MenuIt(itemName, "", itemPrice);
                    currentOrder.addItem(menuItem, quantity);

                } else if (line.equals("---")) {
                    if (currentOrder != null) {
                        currentOrder.setPaymentStrategy(new com.restaurant.model.payment.CashPaymentStrategy());
                        currentOrder.processPayment();
                    }
                    currentOrder = null;
                }
            }

            System.out.println("✅ Historiques de " + allOrders.size() + " client(s) chargés");

        } catch (IOException e) {
            System.out.println("❌ Erreur chargement historiques: " + e.getMessage());
        }

        return allOrders;
    }
}