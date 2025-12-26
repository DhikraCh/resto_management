package com.restaurant.view;

import com.restaurant.model.RestaurantSystem;
import com.restaurant.model.UserSession;
import com.restaurant.model.OrdersManager;
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
import java.util.stream.Collectors;

/**
 * VUE - Dashboard administrateur avec gestion des commandes
 */
public class AdminView {
    private Stage stage;
    private ListView<HBox> pendingOrdersListView;
    private ListView<String> historyListView;
    private Label totalSalesLabel;
    private Label ordersCountLabel;
    private Label popularDishLabel;
    private Label pendingCountLabel;

    public AdminView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        root.setTop(createHeader());
        root.setCenter(createMainContent());

        Scene scene = new Scene(root, 1400, 750);
        stage.setTitle("Dashboard Administrateur");
        stage.setScene(scene);
        stage.show();

        loadStatistics();
        loadOrders();
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

        Button refreshButton = new Button("🔄 Actualiser");
        refreshButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #F7931E; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        refreshButton.setOnAction(e -> {
            loadStatistics();
            loadOrders();
        });

        Button logoutButton = new Button("🚪 Déconnexion");
        logoutButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        logoutButton.setOnAction(e -> logout());

        titleBox.getChildren().addAll(backButton, spacer1, title, spacer2, emailLabel, refreshButton, logoutButton);
        header.getChildren().add(titleBox);

        return header;
    }

    private HBox createMainContent() {
        HBox mainContent = new HBox(15);
        mainContent.setPadding(new Insets(15));

        mainContent.getChildren().addAll(
                createStatisticsPanel(),
                createPendingOrdersPanel(),
                createHistoryPanel()
        );

        return mainContent;
    }

    private VBox createStatisticsPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(280);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label title = new Label("📊 STATISTIQUES");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        // En attente
        VBox pendingBox = createStatBox("⏳ En Attente", "0", "#F7931E");
        pendingCountLabel = (Label) ((VBox) pendingBox.getChildren().get(1)).getChildren().get(0);

        // Total ventes
        VBox salesBox = createStatBox("💰 Total Ventes", "0.00 DA", "#27ae60");
        totalSalesLabel = (Label) ((VBox) salesBox.getChildren().get(1)).getChildren().get(0);

        // Nombre commandes
        VBox ordersBox = createStatBox("📦 Commandes", "0", "#3498db");
        ordersCountLabel = (Label) ((VBox) ordersBox.getChildren().get(1)).getChildren().get(0);

        // Plat populaire
        VBox dishBox = createStatBox("🏆 Plus Populaire", "Aucun", "#e74c3c");
        popularDishLabel = (Label) ((VBox) dishBox.getChildren().get(1)).getChildren().get(0);

        panel.getChildren().addAll(title, pendingBox, salesBox, ordersBox, dishBox);
        return panel;
    }

    private VBox createStatBox(String label, String value, String color) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: " + color + "; " +
                "-fx-background-radius: 8;");

        Label labelText = new Label(label);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        labelText.setTextFill(Color.WHITE);

        VBox valueBox = new VBox();
        Label valueText = new Label(value);
        valueText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueText.setTextFill(Color.WHITE);
        valueBox.getChildren().add(valueText);

        box.getChildren().addAll(labelText, valueBox);
        return box;
    }

    private VBox createPendingOrdersPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(520);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label title = new Label("⏳ COMMANDES EN ATTENTE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#F7931E"));

        pendingOrdersListView = new ListView<>();
        pendingOrdersListView.setPrefHeight(600);
        pendingOrdersListView.setStyle("-fx-font-size: 12px;");

        panel.getChildren().addAll(title, pendingOrdersListView);
        return panel;
    }

    private VBox createHistoryPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(520);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label title = new Label("📚 HISTORIQUE COMPLET");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2c3e50"));

        historyListView = new ListView<>();
        historyListView.setPrefHeight(600);
        historyListView.setStyle("-fx-font-size: 11px; -fx-font-family: 'Courier New';");

        panel.getChildren().addAll(title, historyListView);
        return panel;
    }

    private void loadStatistics() {
        RestaurantSystem system = RestaurantSystem.getInstance();
        List<Order> orders = system.getOrders();

        // Compter en attente
        long pendingCount = orders.stream().filter(Order::isPending).count();

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
        pendingCountLabel.setText(String.valueOf(pendingCount));
        totalSalesLabel.setText(String.format("%.0f DA", totalSales));
        ordersCountLabel.setText(String.valueOf(ordersCount));
        popularDishLabel.setText(popularDish);
    }

    private void loadOrders() {
        RestaurantSystem system = RestaurantSystem.getInstance();
        List<Order> allOrders = system.getOrders();

        // Commandes en attente
        List<Order> pendingOrders = allOrders.stream()
                .filter(Order::isPending)
                .collect(Collectors.toList());

        pendingOrdersListView.getItems().clear();

        if (pendingOrders.isEmpty()) {
            Label emptyLabel = new Label("Aucune commande en attente");
            emptyLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            HBox emptyBox = new HBox(emptyLabel);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(20));
            pendingOrdersListView.getItems().add(emptyBox);
        } else {
            for (Order order : pendingOrders) {
                pendingOrdersListView.getItems().add(createPendingOrderBox(order));
            }
        }

        // Historique complet
        historyListView.getItems().clear();

        if (allOrders.isEmpty()) {
            historyListView.getItems().add("Aucune commande pour le moment");
        } else {
            for (Order order : allOrders) {
                if (order.isPaid()) {
                    historyListView.getItems().add(formatHistoryOrder(order));
                    historyListView.getItems().add("─────────────────────────────────────");
                }
            }
        }
    }

    private HBox createPendingOrderBox(Order order) {
        VBox orderInfo = new VBox(8);
        orderInfo.setPadding(new Insets(12));
        orderInfo.setStyle("-fx-background-color: #fff8dc; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #F7931E; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;");

        // Header
        Label headerLabel = new Label("⏳ Commande #" + order.getOrderId() + " | " + order.getFormattedTime());
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        headerLabel.setTextFill(Color.web("#F7931E"));

        // Items
        VBox itemsBox = new VBox(3);
        for (OrderItem item : order.getItems()) {
            Label itemLabel = new Label("  • " + item.getQuantity() + "x " +
                    item.getMenuItem().getName() + " - " +
                    item.getSubtotal() + " DA");
            itemLabel.setFont(Font.font("Arial", 11));
            itemsBox.getChildren().add(itemLabel);
        }

        // Total et paiement
        String paymentInfo = order.isOnsitePayment() ? "[À PAYER SUR PLACE]" : "[PAYÉ]";
        Label totalLabel = new Label("TOTAL: " + order.getTotal() + " DA " + paymentInfo);
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        totalLabel.setTextFill(Color.web("#2c3e50"));

        // Boutons d'action selon le type de paiement
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(5, 0, 0, 0));

        if (order.isOnsitePayment()) {
            // SUR PLACE : Seulement bouton VALIDER
            Button validateButton = new Button("✅ Valider (prêt à récupérer)");
            validateButton.setStyle("-fx-font-size: 11px; -fx-background-color: #27ae60; " +
                    "-fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
            validateButton.setPrefWidth(220);
            validateButton.setOnAction(e -> handleValidateOrder(order));
            buttonsBox.getChildren().add(validateButton);
        } else {
            // ESPÈCES ou CARTE : Seulement bouton LIVRAISON
            Button deliverButton = new Button("🚚 Assigner à la Livraison");
            deliverButton.setStyle("-fx-font-size: 11px; -fx-background-color: #3498db; " +
                    "-fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
            deliverButton.setPrefWidth(220);
            deliverButton.setOnAction(e -> handleDeliverOrder(order));
            buttonsBox.getChildren().add(deliverButton);
        }

        orderInfo.getChildren().addAll(headerLabel, itemsBox, totalLabel, buttonsBox);

        HBox container = new HBox(orderInfo);
        container.setPadding(new Insets(5));
        return container;
    }

    private String formatHistoryOrder(Order order) {
        StringBuilder sb = new StringBuilder();

        // Status avec couleur
        String statusIcon = "";
        switch (order.getStatus()) {
            case PENDING: statusIcon = "⏳"; break;
            case VALIDATED: statusIcon = "✅"; break;
            case DELIVERED: statusIcon = "🚚"; break;
        }

        sb.append(statusIcon).append(" Commande #").append(order.getOrderId())
                .append(" | ").append(order.getFormattedTime()).append("\n");

        for (OrderItem item : order.getItems()) {
            sb.append("  • ").append(item.getQuantity())
                    .append("x ").append(item.getMenuItem().getName())
                    .append(" - ").append(item.getSubtotal()).append(" DA\n");
        }

        sb.append("  TOTAL: ").append(order.getTotal()).append(" DA ");

        if (order.isOnsitePayment()) {
            sb.append("[À PAYER SUR PLACE] ");
        } else {
            sb.append("[PAYÉ] ");
        }

        sb.append(order.getStatusText());

        if (order.getProcessedTime() != null) {
            sb.append("\n  Traité le: ").append(order.getFormattedProcessedTime());
        }

        return sb.toString();
    }

    private void handleValidateOrder(Order order) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Valider la commande");
        confirm.setHeaderText("Commande #" + order.getOrderId());
        confirm.setContentText("Voulez-vous valider cette commande ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                order.validate();
                // Sauvegarder les changements
                OrdersManager.getInstance().saveOrders(RestaurantSystem.getInstance().getOrders());
                // Rafraîchir l'affichage
                loadStatistics();
                loadOrders();
                showSuccessAlert("Commande #" + order.getOrderId() + " validée avec succès !");
            }
        });
    }

    private void handleDeliverOrder(Order order) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Assigner à la livraison");
        confirm.setHeaderText("Commande #" + order.getOrderId());
        confirm.setContentText("Voulez-vous assigner cette commande à la livraison ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                order.assignDelivery();
                // Sauvegarder les changements
                OrdersManager.getInstance().saveOrders(RestaurantSystem.getInstance().getOrders());
                // Rafraîchir l'affichage
                loadStatistics();
                loadOrders();
                showSuccessAlert("Commande #" + order.getOrderId() + " assignée à la livraison !");
            }
        });
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

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}