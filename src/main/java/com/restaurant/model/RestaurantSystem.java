package com.restaurant.model;

import com.restaurant.model.menu.*;
import com.restaurant.model.order.Order;
import com.restaurant.model.notification.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRON SINGLETON - Gestion centralisée du système de restaurant
 * Une seule instance pour gérer menus, commandes et notifications
 */
public class RestaurantSystem {
    private static RestaurantSystem instance;

    private MenuComponent mainMenu;
    private List<Order> orders;
    private OrderSubject orderSubject;

    private RestaurantSystem() {
        orders = new ArrayList<>();
        orderSubject = new OrderSubject();
        initializeMenu();
        initializeObservers();
        loadOrders(); // Charger les commandes sauvegardées
    }

    public static RestaurantSystem getInstance() {
        if (instance == null) {
            synchronized (RestaurantSystem.class) {
                if (instance == null) {
                    instance = new RestaurantSystem();
                }
            }
        }
        return instance;
    }

    private void initializeMenu() {
        mainMenu = new MenuCategory("Menu Principal", "Tous nos plats");

        MenuCategory entrees = new MenuCategory("Entrées", "Pour commencer");
        entrees.add(new MenuIt("Chorba", "Soupe traditionnelle algérienne", 350));
        entrees.add(new MenuIt("Bourek", "Feuilles farcies à la viande", 250));
        entrees.add(new MenuIt("Salade Mixte", "Tomates, concombres, oignons", 200));

        MenuCategory plats = new MenuCategory("Plats Principaux", "Nos spécialités");
        plats.add(new MenuIt("Couscous", "Couscous traditionnel aux légumes", 800));
        plats.add(new MenuIt("Tajine", "Tajine de poulet aux olives", 900));
        plats.add(new MenuIt("Rechta", "Pâtes fraîches sauce blanche", 700));
        plats.add(new MenuIt("Garantita", "Galette de pois chiches", 300));

        MenuCategory desserts = new MenuCategory("Desserts", "Pour terminer en beauté");
        desserts.add(new MenuIt("Baklawa", "Pâtisserie au miel et amandes", 400));
        desserts.add(new MenuIt("Makroud", "Gâteau aux dattes et miel", 350));
        desserts.add(new MenuIt("Zlabia", "Beignets au miel", 300));

        MenuCategory boissons = new MenuCategory("Boissons", "Boissons chaudes et froides");
        boissons.add(new MenuIt("Thé à la menthe", "Thé traditionnel", 150));
        boissons.add(new MenuIt("Café", "Café noir ou au lait", 200));
        boissons.add(new MenuIt("Jus d'orange", "Jus frais pressé", 250));

        mainMenu.add(entrees);
        mainMenu.add(plats);
        mainMenu.add(desserts);
        mainMenu.add(boissons);
    }

    private void initializeObservers() {
        KitchenObserver kitchenObserver = new KitchenObserver();
        orderSubject.attach(kitchenObserver);
    }

    public MenuComponent getMenu() {
        return mainMenu;
    }

    public void addOrder(Order order) {
        orders.add(order);
        OrdersManager.getInstance().appendOrder(order);
    }

    public void notifyOrderValidated(Order order) {
        orderSubject.notifyObservers(order, "ORDER_VALIDATED");
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    private void loadOrders() {
        List<Order> savedOrders = OrdersManager.getInstance().loadOrders();
        orders.addAll(savedOrders);
        System.out.println("📦 " + savedOrders.size() + " commande(s) restaurée(s)");
    }

    public void displayMenu() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         MENU DU RESTAURANT");
        System.out.println("═══════════════════════════════════════\n");
        mainMenu.display(0);
        System.out.println("\n═══════════════════════════════════════\n");
    }
}

