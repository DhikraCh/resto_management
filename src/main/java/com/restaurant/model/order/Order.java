package com.restaurant.model.order;

import com.restaurant.model.menu.MenuIt;
import com.restaurant.model.payment.PaymentStrategy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une commande
 * Intègre la stratégie de paiement (PATRON STRATÉGIE)
 */
public class Order {
    public enum OrderStatus {
        PENDING,    // En attente
        VALIDATED,  // Validée
        DELIVERED   // En livraison
    }

    private static int orderCounter = 1000;

    private int orderId;
    private List<OrderItem> items;
    private LocalDateTime orderTime;
    private LocalDateTime processedTime;
    private PaymentStrategy paymentStrategy;
    private boolean isPaid;
    private String paymentMethod; // "PAID" ou "ONSITE"
    private OrderStatus status;

    public Order() {
        this.orderId = orderCounter++;
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.processedTime = null;
        this.isPaid = false;
        this.paymentMethod = "";
        this.status = OrderStatus.PENDING;
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

        // Déterminer le type de paiement
        String methodName = paymentStrategy.getPaymentMethod();
        if (methodName.contains("sur place")) {
            paymentMethod = "ONSITE";
        } else {
            paymentMethod = "PAID";
        }

        return isPaid;
    }

    // Marquer comme validée
    public void validate() {
        this.status = OrderStatus.VALIDATED;
        this.processedTime = LocalDateTime.now();
    }

    // Marquer comme en livraison
    public void assignDelivery() {
        this.status = OrderStatus.DELIVERED;
        this.processedTime = LocalDateTime.now();
    }

    // Getters
    public int getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public LocalDateTime getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(LocalDateTime time) {
        this.processedTime = time;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String method) {
        this.paymentMethod = method;
    }

    public boolean isOnsitePayment() {
        return "ONSITE".equals(paymentMethod);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    public boolean isValidated() {
        return status == OrderStatus.VALIDATED;
    }

    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }

    public String getStatusText() {
        switch (status) {
            case PENDING: return "⏳ EN ATTENTE";
            case VALIDATED: return "✅ VALIDÉE";
            case DELIVERED: return "🚚 EN LIVRAISON";
            default: return "❓ INCONNU";
        }
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return orderTime.format(formatter);
    }

    public String getFormattedProcessedTime() {
        if (processedTime == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return processedTime.format(formatter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Commande #").append(orderId).append(" - ").append(getFormattedTime()).append("\n");
        for (OrderItem item : items) {
            sb.append("  ").append(item.toString()).append("\n");
        }
        sb.append("Total: ").append(getTotal()).append(" DA ");

        if (isOnsitePayment()) {
            sb.append("[À PAYER SUR PLACE] ");
        } else if (isPaid) {
            sb.append("[PAYÉ] ");
        }

        sb.append(getStatusText());

        return sb.toString();
    }
}