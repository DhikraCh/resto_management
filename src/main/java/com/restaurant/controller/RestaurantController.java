package com.restaurant.controller;

import com.restaurant.model.RestaurantSystem;
import com.restaurant.model.UserSession;
import com.restaurant.model.ClientOrderManager;
import com.restaurant.model.menu.*;
import com.restaurant.model.order.Order;
import com.restaurant.model.payment.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CONTRÔLEUR MVC - Gère la logique métier
 * Fait le lien entre le modèle (RestaurantSystem) et la vue
 */
public class RestaurantController {
    private RestaurantSystem system;
    private Order currentOrder;

    public RestaurantController() {
        this.system = RestaurantSystem.getInstance();
        this.currentOrder = null;
    }

    public MenuComponent getMenu() {
        return system.getMenu();
    }

    public void displayMenu() {
        system.displayMenu();
    }

    public void createNewOrder() {
        currentOrder = new Order();
        System.out.println("✅ Nouvelle commande créée #" + currentOrder.getOrderId());
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public boolean hasCurrentOrder() {
        return currentOrder != null && !currentOrder.getItems().isEmpty();
    }

    public void addItemToOrder(MenuIt item, int quantity) {
        if (currentOrder == null) {
            createNewOrder();
        }
        currentOrder.addItem(item, quantity);
        System.out.println("✅ Ajouté: " + quantity + "x " + item.getName());
    }

    public void removeItemFromOrder(int index) {
        if (currentOrder != null) {
            currentOrder.removeItem(index);
            System.out.println("✅ Article retiré de la commande");
        }
    }

    public double getCurrentOrderTotal() {
        return currentOrder != null ? currentOrder.getTotal() : 0.0;
    }

    public void setPaymentMethod(String method, String details) {
        if (currentOrder == null) {
            System.out.println("❌ Pas de commande en cours");
            return;
        }

        PaymentStrategy strategy = null;
        switch (method.toUpperCase()) {
            case "CASH":
                strategy = new CashPaymentStrategy();
                break;
            case "CARD":
                strategy = new CardPaymentStrategy(details);
                break;
            case "MOBILE":
                strategy = new MobilePaymentStrategy(details);
                break;
            case "ONSITE":
                strategy = new OnsitePaymentStrategy();
                break;
            default:
                System.out.println("❌ Méthode de paiement inconnue");
                return;
        }

        currentOrder.setPaymentStrategy(strategy);
        System.out.println("✅ Méthode de paiement sélectionnée: " + strategy.getPaymentMethod());
    }

    public boolean validateAndPayOrder() {
        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            System.out.println("❌ Commande vide");
            return false;
        }

        System.out.println("\n┌────────────────────────────┐");
        System.out.println("  TRAITEMENT DU PAIEMENT");
        System.out.println("└────────────────────────────┘");

        boolean success = currentOrder.processPayment();

        if (success) {
            system.addOrder(currentOrder);
            system.notifyOrderValidated(currentOrder);

            // Si client connecté, sauvegarder dans son historique
            UserSession session = UserSession.getInstance();
            if (session.isClient() && session.getCurrentEmail() != null) {
                ClientOrderManager.getInstance().saveClientOrder(
                        session.getCurrentEmail(),
                        currentOrder
                );
                session.addOrderToHistory(currentOrder);
            }

            System.out.println("\n✅ Commande validée et payée !");
            System.out.println("└────────────────────────────┘\n");

            currentOrder = null;
            return true;
        }

        return false;
    }

    public List<MenuIt> getAllMenuItems() {
        List<MenuIt> items = new ArrayList<>();
        collectMenuItems(system.getMenu(), items);
        return items;
    }

    private void collectMenuItems(MenuComponent component, List<MenuIt> items) {
        if (component instanceof MenuIt) {
            items.add((MenuIt) component);
        } else if (component instanceof MenuCategory) {
            MenuCategory category = (MenuCategory) component;
            for (MenuComponent child : category.getComponents()) {
                collectMenuItems(child, items);
            }
        }
    }

    public List<MenuCategory> getMenuCategories() {
        List<MenuCategory> categories = new ArrayList<>();
        MenuComponent menu = system.getMenu();
        if (menu instanceof MenuCategory) {
            MenuCategory mainMenu = (MenuCategory) menu;
            for (MenuComponent child : mainMenu.getComponents()) {
                if (child instanceof MenuCategory) {
                    categories.add((MenuCategory) child);
                }
            }
        }
        return categories;
    }

    public List<Order> getOrderHistory() {
        // Si client connecté, retourner son historique personnel
        UserSession session = UserSession.getInstance();
        if (session.isClient() && session.getCurrentEmail() != null) {
            return ClientOrderManager.getInstance().loadClientOrders(session.getCurrentEmail());
        }
        // Sinon retourner l'historique global (pour admins/guests)
        return system.getOrders();
    }
}