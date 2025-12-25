package com.restaurant.view;

import com.restaurant.model.RestaurantSystem;
import com.restaurant.model.UserSession;
import com.restaurant.model.order.Order;
import com.restaurant.model.order.OrderItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VUE - Dashboard administrateur
 */
public class AdminView {
    private Stage stage;
    private ListView<String> ordersListView;
    private Label totalSalesLabel;
    private Label ordersCountLabel;
    private Label popularDishLabel;

    public AdminView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        root.setTop(createHeader());
        root.setCenter(createMainContent());

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Dashboard Administrateur");
        stage.setScene(scene);
        stage.show();

        loadStatistics();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #34495e); " +
                "-fx-background-radius: 10;");

        HBox titleBox = new HBox(20);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("🏠 Retour à l'accueil");
        backButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: white; -fx-text-fill: #2c3e50; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        backButton.setOnAction(e -> handleBackToWelcome());

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label title = new Label("👨‍💼 DASHBOARD ADMINISTRATEUR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label emailLabel = new Label("📧 " + UserSession.getInstance().getCurrentAdminEmail());
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        emailLabel.setTextFill(Color.web("#ecf0f1"));

        Button logoutButton = new Button("🚪 Déconnexion");
        logoutButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        logoutButton.setOnAction(e -> logout());

        titleBox.getChildren().addAll(backButton, spacer1, title, spacer2, emailLabel, logoutButton);
        header.getChildren().add(titleBox);

        return header;
    }

    private void handleBackToWelcome() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Retour à l'accueil");
        confirm.setContentText("Voulez-vous vraiment revenir à l'accueil ?\n\nVous resterez connecté.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // NE PAS déconnecter - juste retourner
                new WelcomeView(stage).show();
            }
        });
    }

    private HBox createMainContent() {
        HBox mainContent = new HBox(15);
        mainContent.setPadding(new Insets(15));

        mainContent.getChildren().addAll(
                createStatisticsPanel(),
                createOrdersPanel()
        );

        return mainContent;
    }

    private VBox createStatisticsPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(350);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label title = new Label("📊 STATISTIQUES");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        // Total ventes
        VBox salesBox = createStatBox("💰 Total des Ventes", "0.00 DA", "#27ae60");
        totalSalesLabel = (Label) ((VBox) salesBox.getChildren().get(1)).getChildren().get(0);

        // Nombre commandes
        VBox ordersBox = createStatBox("📦 Nombre de Commandes", "0", "#3498db");
        ordersCountLabel = (Label) ((VBox) ordersBox.getChildren().get(1)).getChildren().get(0);

        // Plat populaire
        VBox dishBox = createStatBox("🏆 Plat le Plus Populaire", "Aucun", "#e74c3c");
        popularDishLabel = (Label) ((VBox) dishBox.getChildren().get(1)).getChildren().get(0);

        Button refreshButton = new Button("🔄 Actualiser");
        refreshButton.setPrefWidth(310);
        refreshButton.setPrefHeight(40);
        refreshButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-color: #F7931E; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        refreshButton.setOnAction(e -> loadStatistics());

        panel.getChildren().addAll(title, salesBox, ordersBox, dishBox, refreshButton);
        return panel;
    }

    private VBox createStatBox(String label, String value, String color) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: " + color + "; " +
                "-fx-background-radius: 8;");

        Label labelText = new Label(label);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        labelText.setTextFill(Color.WHITE);

        VBox valueBox = new VBox();
        Label valueText = new Label(value);
        valueText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueText.setTextFill(Color.WHITE);
        valueBox.getChildren().add(valueText);

        box.getChildren().addAll(labelText, valueBox);
        return box;
    }

    private VBox createOrdersPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(600);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label title = new Label("📋 HISTORIQUE DES COMMANDES");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        ordersListView = new ListView<>();
        ordersListView.setPrefHeight(450);
        ordersListView.setStyle("-fx-font-size: 13px; -fx-font-family: 'Courier New';");

        panel.getChildren().addAll(title, ordersListView);
        return panel;
    }

    private void loadStatistics() {
        RestaurantSystem system = RestaurantSystem.getInstance();
        List<Order> orders = system.getOrders();

        // Calculer total ventes
        double totalSales = orders.stream()
                .filter(Order::isPaid)
                .mapToDouble(Order::getTotal)
                .sum();

        // Nombre de commandes
        int ordersCount = (int) orders.stream()
                .filter(Order::isPaid)
                .count();

        // Plat le plus populaire
        Map<String, Integer> dishCount = new HashMap<>();
        for (Order order : orders) {
            if (order.isPaid()) {
                for (OrderItem item : order.getItems()) {
                    String dishName = item.getMenuItem().getName();
                    dishCount.put(dishName, dishCount.getOrDefault(dishName, 0) + item.getQuantity());
                }
            }
        }

        String popularDish = "Aucun";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : dishCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                popularDish = entry.getKey() + " (" + maxCount + "x)";
            }
        }

        // Mettre à jour les labels
        totalSalesLabel.setText(String.format("%.2f DA", totalSales));
        ordersCountLabel.setText(String.valueOf(ordersCount));
        popularDishLabel.setText(popularDish);

        // Charger la liste des commandes
        ordersListView.getItems().clear();
        if (orders.isEmpty()) {
            ordersListView.getItems().add("Aucune commande pour le moment");
        } else {
            for (Order order : orders) {
                if (order.isPaid()) {
                    ordersListView.getItems().add(formatOrder(order));
                    ordersListView.getItems().add("─────────────────────────────────");
                }
            }
        }
    }

    private String formatOrder(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Commande #").append(order.getOrderId())
                .append(" | ").append(order.getFormattedTime()).append("\n");

        for (OrderItem item : order.getItems()) {
            sb.append("  • ").append(item.getQuantity())
                    .append("x ").append(item.getMenuItem().getName())
                    .append(" - ").append(item.getSubtotal()).append(" DA\n");
        }

        sb.append("  TOTAL: ").append(order.getTotal()).append(" DA ");

        // Afficher le statut selon le type de paiement
        if (order.isOnsitePayment()) {
            sb.append("[À PAYER SUR PLACE]");
        } else if (order.isPaid()) {
            sb.append("[PAYÉ]");
        } else {
            sb.append("[NON PAYÉ]");
        }

        return sb.toString();
    }

    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText("Voulez-vous vraiment vous déconnecter ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserSession.getInstance().logout();
                new WelcomeView(stage).show();
            }
        });
    }
}