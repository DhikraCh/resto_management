package com.restaurant.view;

import com.restaurant.model.*;
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
 * VUE - Dashboard administrateur avec gestion des comptes et commandes
 */
public class AdminView {
    private Stage stage;
    private TabPane tabPane;

    // Onglet Comptes
    private ListView<HBox> pendingUsersListView;
    private Label pendingUsersCountLabel;

    // Onglet Commandes
    private ListView<HBox> pendingOrdersListView;
    private ListView<String> historyListView;
    private Label totalSalesLabel;
    private Label ordersCountLabel;
    private Label popularDishLabel;
    private Label pendingOrdersCountLabel;

    public AdminView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        root.setTop(createHeader());
        root.setCenter(createTabContent());

        Scene scene = new Scene(root, 1400, 750);
        stage.setTitle("Dashboard Administrateur");
        stage.setScene(scene);
        stage.show();

        loadStatistics();
        loadOrders();
        loadPendingUsers();
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

        Label emailLabel = new Label("📧 " + UserSession.getInstance().getCurrentEmail());
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        emailLabel.setTextFill(Color.web("#ecf0f1"));

        Button notificationsButton = new Button("🔔 Notifications");
        notificationsButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #F7931E; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        notificationsButton.setOnAction(e -> showNotifications());

        Button logoutButton = new Button("🚪 Déconnexion");
        logoutButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        logoutButton.setOnAction(e -> logout());

        titleBox.getChildren().addAll(backButton, spacer1, title, spacer2, emailLabel, notificationsButton, logoutButton);
        header.getChildren().add(titleBox);

        return header;
    }

    private TabPane createTabContent() {
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Onglet 1: Gestion des comptes
        Tab usersTab = new Tab("👥 Gestion des Comptes");
        usersTab.setContent(createUsersManagementContent());

        // Onglet 2: Gestion des commandes
        Tab ordersTab = new Tab("📦 Gestion des Commandes");
        ordersTab.setContent(createOrdersManagementContent());

        tabPane.getTabs().addAll(usersTab, ordersTab);

        return tabPane;
    }

    // ONGLET 1: GESTION DES COMPTES
    private HBox createUsersManagementContent() {
        HBox content = new HBox(20);
        content.setPadding(new Insets(15));

        // Statistiques
        VBox statsPanel = new VBox(15);
        statsPanel.setPrefWidth(250);
        statsPanel.setPadding(new Insets(20));
        statsPanel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label statsTitle = new Label("📊 STATISTIQUES");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        statsTitle.setTextFill(Color.web("#2c3e50"));

        VBox pendingBox = createStatBox("⏳ En Attente", "0", "#F7931E");
        pendingUsersCountLabel = (Label) ((VBox) pendingBox.getChildren().get(1)).getChildren().get(0);

        statsPanel.getChildren().addAll(statsTitle, pendingBox);

        // Panel des demandes
        VBox requestsPanel = new VBox(15);
        requestsPanel.setPrefWidth(1050);
        requestsPanel.setPadding(new Insets(20));
        requestsPanel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label requestsTitle = new Label("⏳ DEMANDES D'INSCRIPTION EN ATTENTE");
        requestsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        requestsTitle.setTextFill(Color.web("#F7931E"));

        pendingUsersListView = new ListView<>();
        pendingUsersListView.setPrefHeight(580);
        pendingUsersListView.setStyle("-fx-font-size: 12px;");

        requestsPanel.getChildren().addAll(requestsTitle, pendingUsersListView);

        content.getChildren().addAll(statsPanel, requestsPanel);
        return content;
    }

    // ONGLET 2: GESTION DES COMMANDES
    private HBox createOrdersManagementContent() {
        HBox content = new HBox(15);
        content.setPadding(new Insets(15));

        content.getChildren().addAll(
                createStatisticsPanel(),
                createPendingOrdersPanel(),
                createHistoryPanel()
        );

        return content;
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

        VBox pendingBox = createStatBox("⏳ En Attente", "0", "#F7931E");
        pendingOrdersCountLabel = (Label) ((VBox) pendingBox.getChildren().get(1)).getChildren().get(0);

        VBox salesBox = createStatBox("💰 Total Ventes", "0.00 DA", "#27ae60");
        totalSalesLabel = (Label) ((VBox) salesBox.getChildren().get(1)).getChildren().get(0);

        VBox ordersBox = createStatBox("📦 Commandes", "0", "#3498db");
        ordersCountLabel = (Label) ((VBox) ordersBox.getChildren().get(1)).getChildren().get(0);

        VBox dishBox = createStatBox("🏆 Plus Populaire", "Aucun", "#e74c3c");
        popularDishLabel = (Label) ((VBox) dishBox.getChildren().get(1)).getChildren().get(0);

        panel.getChildren().addAll(title, pendingBox, salesBox, ordersBox, dishBox);
        return panel;
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
        pendingOrdersListView.setPrefHeight(550);
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
        historyListView.setPrefHeight(550);
        historyListView.setStyle("-fx-font-size: 11px; -fx-font-family: 'Courier New';");

        panel.getChildren().addAll(title, historyListView);
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

    // Charger les utilisateurs en attente
    private void loadPendingUsers() {
        UserManager userManager = UserManager.getInstance();
        List<User> pendingUsers = userManager.getPendingUsers();

        pendingUsersCountLabel.setText(String.valueOf(pendingUsers.size()));

        pendingUsersListView.getItems().clear();

        if (pendingUsers.isEmpty()) {
            Label emptyLabel = new Label("Aucune demande en attente");
            emptyLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            HBox emptyBox = new HBox(emptyLabel);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(20));
            pendingUsersListView.getItems().add(emptyBox);
        } else {
            for (User user : pendingUsers) {
                pendingUsersListView.getItems().add(createPendingUserBox(user));
            }
        }
    }

    private HBox createPendingUserBox(User user) {
        VBox userInfo = new VBox(8);
        userInfo.setPadding(new Insets(12));
        userInfo.setStyle("-fx-background-color: #fff8dc; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #F7931E; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;");

        Label headerLabel = new Label("⏳ " + user.getRoleDisplay() + " - " + user.getEmail());
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        headerLabel.setTextFill(Color.web("#F7931E"));

        Label statusLabel = new Label(user.getStatusDisplay());
        statusLabel.setFont(Font.font("Arial", 11));
        statusLabel.setTextFill(Color.web("#7f8c8d"));

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(5, 0, 0, 0));

        Button approveButton = new Button("✅ Approuver");
        approveButton.setStyle("-fx-font-size: 11px; -fx-background-color: #27ae60; " +
                "-fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        approveButton.setPrefWidth(140);
        approveButton.setOnAction(e -> handleApproveUser(user));

        Button rejectButton = new Button("❌ Rejeter");
        rejectButton.setStyle("-fx-font-size: 11px; -fx-background-color: #e74c3c; " +
                "-fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        rejectButton.setPrefWidth(140);
        rejectButton.setOnAction(e -> handleRejectUser(user));

        buttonsBox.getChildren().addAll(approveButton, rejectButton);

        userInfo.getChildren().addAll(headerLabel, statusLabel, buttonsBox);

        HBox container = new HBox(userInfo);
        container.setPadding(new Insets(5));
        HBox.setHgrow(userInfo, Priority.ALWAYS);
        return container;
    }

    private void handleApproveUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approuver le compte");
        confirm.setHeaderText(user.getRoleDisplay() + " - " + user.getEmail());
        confirm.setContentText("Voulez-vous approuver ce compte ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserManager.getInstance().approveUser(user.getEmail());
                loadPendingUsers();
                showSuccessAlert("Compte approuvé avec succès !");
            }
        });
    }

    private void handleRejectUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Rejeter le compte");
        confirm.setHeaderText(user.getRoleDisplay() + " - " + user.getEmail());
        confirm.setContentText("Voulez-vous rejeter ce compte ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserManager.getInstance().rejectUser(user.getEmail());
                loadPendingUsers();
                showSuccessAlert("Compte rejeté.");
            }
        });
    }

    private void loadStatistics() {
        RestaurantSystem system = RestaurantSystem.getInstance();
        List<Order> orders = system.getOrders();

        long pendingCount = orders.stream().filter(Order::isPending).count();

        double totalSales = orders.stream()
                .filter(Order::isPaid)
                .mapToDouble(Order::getTotal)
                .sum();

        int ordersCount = (int) orders.stream()
                .filter(Order::isPaid)
                .count();

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

        pendingOrdersCountLabel.setText(String.valueOf(pendingCount));
        totalSalesLabel.setText(String.format("%.0f DA", totalSales));
        ordersCountLabel.setText(String.valueOf(ordersCount));
        popularDishLabel.setText(popularDish);
    }

    private void loadOrders() {
        RestaurantSystem system = RestaurantSystem.getInstance();
        List<Order> allOrders = system.getOrders();

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

        Label headerLabel = new Label("⏳ Commande #" + order.getOrderId() + " | " + order.getFormattedTime());
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        headerLabel.setTextFill(Color.web("#F7931E"));

        VBox itemsBox = new VBox(3);
        for (OrderItem item : order.getItems()) {
            Label itemLabel = new Label("  • " + item.getQuantity() + "x " +
                    item.getMenuItem().getName() + " - " +
                    item.getSubtotal() + " DA");
            itemLabel.setFont(Font.font("Arial", 11));
            itemsBox.getChildren().add(itemLabel);
        }

        String paymentInfo = order.isOnsitePayment() ? "[À PAYER SUR PLACE]" : "[PAYÉ]";
        Label totalLabel = new Label("TOTAL: " + order.getTotal() + " DA " + paymentInfo);
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        totalLabel.setTextFill(Color.web("#2c3e50"));

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(5, 0, 0, 0));

        if (order.isOnsitePayment()) {
            Button validateButton = new Button("✅ Valider (prêt à récupérer)");
            validateButton.setStyle("-fx-font-size: 11px; -fx-background-color: #27ae60; " +
                    "-fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
            validateButton.setPrefWidth(220);
            validateButton.setOnAction(e -> handleValidateOrder(order));
            buttonsBox.getChildren().add(validateButton);
        } else {
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
                OrdersManager.getInstance().saveOrders(RestaurantSystem.getInstance().getOrders());
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
                OrdersManager.getInstance().saveOrders(RestaurantSystem.getInstance().getOrders());
                loadStatistics();
                loadOrders();
                showSuccessAlert("Commande #" + order.getOrderId() + " assignée à la livraison !");
            }
        });
    }

    private void showNotifications() {
        List<String> notifications = com.restaurant.model.DeliveryNotificationManager.getInstance().getNotifications();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications de Livraison");
        alert.setHeaderText("📬 Historique des livraisons");

        if (notifications.isEmpty()) {
            alert.setContentText("Aucune notification pour le moment.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String notif : notifications) {
                sb.append(notif).append("\n");
            }
            alert.setContentText(sb.toString());
        }

        alert.showAndWait();
    }

    private void handleBackToWelcome() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Retour à l'accueil");
        confirm.setContentText("Voulez-vous vraiment revenir à l'accueil ?\n\nVous resterez connecté.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
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