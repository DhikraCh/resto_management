package com.restaurant.model.order;

import com.restaurant.model.menu.MenuIt;
import com.restaurant.model.payment.PaymentStrategy;
import com.restaurant.model.payment.OnsitePaymentStrategy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une commande
 * Intègre la stratégie de paiement (PATRON STRATÉGIE)
 */
public class Order {
    private static int orderCounter = 1000;

    private int orderId;
    private List<OrderItem> items;
    private LocalDateTime orderTime;
    private PaymentStrategy paymentStrategy;
    private boolean isPaid;

    public Order() {
        this.orderId = orderCounter++;
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.isPaid = false;
    }

    public void addItem(MenuIt menuItem, int quantity) {
        OrderItem orderItem = new OrderItem(menuItem, quantity);
        items.add(orderItem);
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
    }

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    public boolean processPayment() {
        if (paymentStrategy == null) {
            System.out.println("❌ Aucune méthode de paiement sélectionnée");
            return false;
        }

        if (items.isEmpty()) {
            System.out.println("❌ Commande vide");
            return false;
        }

        isPaid = paymentStrategy.pay(getTotal());
        return isPaid;
    }

    public int getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return orderTime.format(formatter);
    }

    // --- Méthodes ajoutées pour résoudre les erreurs de compilation ---
    public String getPaymentMethod() {
        if (paymentStrategy == null) return "Aucune";
        return paymentStrategy.getPaymentMethod();
    }

    public boolean isOnsitePayment() {
        return paymentStrategy instanceof OnsitePaymentStrategy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Commande #").append(orderId).append(" - ").append(getFormattedTime()).append("\n");
        for (OrderItem item : items) {
            sb.append("  ").append(item.toString()).append("\n");
        }
        sb.append("Total: ").append(getTotal()).append(" DA");
        if (isPaid) {
            sb.append(" [PAYÉ]");
        }
        return sb.toString();
    }
}
