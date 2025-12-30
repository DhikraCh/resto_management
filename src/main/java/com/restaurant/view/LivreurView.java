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
import java.util.List;
import java.util.stream.Collectors;

/**
 * VUE - Interface Livreur
 * Affiche les commandes assignées à la livraison
 */
public class LivreurView {
    private Stage stage;
    private ListView<HBox> deliveryOrdersListView;
    private Label assignedCountLabel;

    public LivreurView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        root.setTop(createHeader());
        root.setCenter(createMainContent());

        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Espace Livreur");
        stage.setScene(scene);
        stage.show();

        loadOrders();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: linear-gradient(to right, #3498db, #2980b9); " +
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

        Label title = new Label("🚚 ESPACE LIVREUR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label emailLabel = new Label("📧 " + UserSession.getInstance().getCurrentEmail());
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        emailLabel.setTextFill(Color.web("#ecf0f1"));

        Button refreshButton = new Button("🔄 Actualiser");
        refreshButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        refreshButton.setOnAction(e -> loadOrders());

        Button logoutButton = new Button("🚪 Déconnexion");
        logoutButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        logoutButton.setOnAction(e -> logout());

        titleBox.getChildren().addAll(backButton, spacer1, title, spacer2, emailLabel, refreshButton, logoutButton);
        header.getChildren().add(titleBox);

        return header;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        // Statistiques
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        VBox assignedBox = createStatBox("🚚 Assignées à livrer", "0", "#3498db");
        assignedCountLabel = (Label) ((VBox) assignedBox.getChildren().get(1)).getChildren().get(0);

        statsBox.getChildren().add(assignedBox);

        // Panel des commandes
        VBox ordersPanel = new VBox(15);
        ordersPanel.setPadding(new Insets(20));
        ordersPanel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label ordersTitle = new Label("🚚 MES COMMANDES À LIVRER");
        ordersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ordersTitle.setTextFill(Color.web("#3498db"));

        deliveryOrdersListView = new ListView<>();
        deliveryOrdersListView.setPrefHeight(450);
        deliveryOrdersListView.setStyle("-fx-font-size: 12px;");

        ordersPanel.getChildren().addAll(ordersTitle, deliveryOrdersListView);

        content.getChildren().addAll(statsBox, ordersPanel);
        return content;
    }

    private VBox createStatBox(String label, String value, String color) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setPrefWidth(200);
        box.setStyle("-fx-background-color: " + color + "; " +
                "-fx-background-radius: 8;");

        Label labelText = new Label(label);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        labelText.setTextFill(Color.WHITE);

        VBox valueBox = new VBox();
        Label valueText = new Label(value);
        valueText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueText.setTextFill(Color.WHITE);
        valueBox.getChildren().add(valueText);

        box.getChildren().addAll(labelText, valueBox);
        return box;
    }

    private void loadOrders() {
        RestaurantSystem system = RestaurantSystem.getInstance();
        List<Order> allOrders = system.getOrders();

        // Filtrer uniquement les commandes EN LIVRAISON (DELIVERED)
        List<Order> deliveryOrders = allOrders.stream()
                .filter(Order::isDelivered)
                .collect(Collectors.toList());

        // Mettre à jour le compteur
        assignedCountLabel.setText(String.valueOf(deliveryOrders.size()));

        // Afficher les commandes
        deliveryOrdersListView.getItems().clear();

        if (deliveryOrders.isEmpty()) {
            Label emptyLabel = new Label("Aucune commande à livrer pour le moment");
            emptyLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            HBox emptyBox = new HBox(emptyLabel);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(20));
            deliveryOrdersListView.getItems().add(emptyBox);
        } else {
            for (Order order : deliveryOrders) {
                deliveryOrdersListView.getItems().add(createDeliveryOrderBox(order));
            }
        }
    }

    private HBox createDeliveryOrderBox(Order order) {
        VBox orderInfo = new VBox(8);
        orderInfo.setPadding(new Insets(15));
        orderInfo.setStyle("-fx-background-color: #e3f2fd; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #3498db; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;");

        // Header
        Label headerLabel = new Label("🚚 Commande #" + order.getOrderId() + " | " + order.getFormattedTime());
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        headerLabel.setTextFill(Color.web("#3498db"));

        // Status
        Label statusLabel = new Label(order.getStatusText());
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        statusLabel.setTextFill(Color.web("#27ae60"));

        // Items
        VBox itemsBox = new VBox(3);
        itemsBox.setPadding(new Insets(5, 0, 5, 0));
        for (OrderItem item : order.getItems()) {
            Label itemLabel = new Label("  • " + item.getQuantity() + "x " +
                    item.getMenuItem().getName() + " - " +
                    item.getSubtotal() + " DA");
            itemLabel.setFont(Font.font("Arial", 11));
            itemsBox.getChildren().add(itemLabel);
        }

        // Total
        String paymentInfo = order.isOnsitePayment() ? "[À PAYER SUR PLACE]" : "[PAYÉ]";
        Label totalLabel = new Label("TOTAL: " + order.getTotal() + " DA " + paymentInfo);
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        totalLabel.setTextFill(Color.web("#2c3e50"));

        // Date de traitement
        if (order.getProcessedTime() != null) {
            Label processedLabel = new Label("Assignée le: " + order.getFormattedProcessedTime());
            processedLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
            processedLabel.setTextFill(Color.web("#7f8c8d"));
            orderInfo.getChildren().add(processedLabel);
        }

        // Bouton de validation
        Button validateButton = new Button("✅ Marquer comme livrée");
        validateButton.setStyle("-fx-font-size: 12px; -fx-background-color: #27ae60; " +
                "-fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        validateButton.setPrefWidth(200);
        validateButton.setOnAction(e -> handleMarkAsDelivered(order));

        orderInfo.getChildren().addAll(headerLabel, statusLabel, itemsBox, totalLabel, validateButton);

        HBox container = new HBox(orderInfo);
        container.setPadding(new Insets(5));
        HBox.setHgrow(orderInfo, Priority.ALWAYS);
        return container;
    }

    private void handleMarkAsDelivered(Order order) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la livraison");
        confirm.setHeaderText("Commande #" + order.getOrderId());
        confirm.setContentText("Confirmer que cette commande a été livrée ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Pour l'instant, on peut juste la retirer de la liste
                // ou changer son status (tu peux ajouter un nouveau status COMPLETED)
                showSuccessAlert("Commande #" + order.getOrderId() + " marquée comme livrée !");
                loadOrders();
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