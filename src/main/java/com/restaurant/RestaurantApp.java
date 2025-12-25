package com.restaurant;

import com.restaurant.view.WelcomeView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application
 * Architecture MVC avec patrons de conception:
 * - SINGLETON: RestaurantSystem, AdminManager, UserSession (gestion centrale)
 * - COMPOSITION: MenuComponent, MenuCategory, MenuItem (structure hiérarchique du menu)
 * - STRATÉGIE: PaymentStrategy et ses implémentations (modes de paiement)
 * - OBSERVATEUR: OrderObserver, OrderSubject, KitchenObserver (notifications)
 */
public class RestaurantApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║     SYSTÈME DE GESTION DE RESTAURANT                ║");
        System.out.println("║     Architecture: MVC + Patrons de Conception       ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("✅ PATRONS IMPLÉMENTÉS:");
        System.out.println("   1. SINGLETON      → RestaurantSystem, AdminManager, UserSession");
        System.out.println("   2. COMPOSITION    → MenuComponent, MenuCategory, MenuItem");
        System.out.println("   3. STRATÉGIE      → PaymentStrategy (Cash, Card, Onsite)");
        System.out.println("   4. OBSERVATEUR    → OrderSubject, KitchenObserver");
        System.out.println();
        System.out.println("🏗️ ARCHITECTURE MVC:");
        System.out.println("   - MODEL:      RestaurantSystem, Order, Menu, AdminManager...");
        System.out.println("   - VIEW:       WelcomeView, LoginView, RegisterView, AdminView, RestaurantView");
        System.out.println("   - CONTROLLER: RestaurantController");
        System.out.println();
        System.out.println("🚀 Démarrage de l'interface graphique...");
        System.out.println("════════════════════════════════════════════════════════\n");

        WelcomeView welcomeView = new WelcomeView(primaryStage);
        welcomeView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}