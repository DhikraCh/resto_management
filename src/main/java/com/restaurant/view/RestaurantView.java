package com.restaurant.view;

import com.restaurant.controller.RestaurantController;
import com.restaurant.model.menu.*;
import com.restaurant.model.order.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * VUE MVC - Interface utilisateur JavaFX
 * Affiche les données et capture les actions utilisateur
 */
public class RestaurantView {
    private RestaurantController controller;
    private Stage stage;

    private ListView<MenuIt> menuListView;
    private ListView<OrderItem> orderListView;
    private Label totalLabel;
    private TextArea outputArea;
    private ComboBox<String> paymentMethodCombo;
    private VBox paymentFieldsContainer;
    private TextField quantityField;

    public RestaurantView(RestaurantController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        root.setTop(createHeader());
        root.setCenter(createMainContent());
        root.setBottom(createConsoleOutput());

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Système de Restaurant");
        stage.setScene(scene);
        stage.show();

        loadMenu();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: linear-gradient(to right, #FF6B35, #F7931E); " +
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

        Label title = new Label("🍽️ SYSTÈME DE RESTAURANT 🍽️");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        titleBox.getChildren().addAll(backButton, spacer1, title, spacer2);
        header.getChildren().addAll(titleBox, new Separator());
        return header;
    }

    private void handleBackToWelcome() {
        // Vérifier s'il y a une commande en cours
        if (controller.hasCurrentOrder()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Commande en cours");
            confirm.setContentText("Vous avez une commande en cours.\nVoulez-vous vraiment revenir à l'accueil ?\n\nLa commande sera perdue si elle n'est pas validée.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    returnToWelcome();
                }
            });
        } else {
            returnToWelcome();
        }
    }

    private void returnToWelcome() {
        new WelcomeView(stage).show();
    }

    private HBox createMainContent() {
        HBox mainContent = new HBox(15);
        mainContent.setPadding(new Insets(15));

        mainContent.getChildren().addAll(
                createMenuPanel(),
                createOrderPanel(),
                createPaymentPanel()
        );

        return mainContent;
    }

    private VBox createMenuPanel() {
        VBox panel = new VBox(12);
        panel.setPrefWidth(400);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label titleLabel = new Label("📋 MENU");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        ComboBox<MenuCategory> categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Sélectionner une catégorie");
        categoryCombo.setPrefWidth(370);
        categoryCombo.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        menuListView = new ListView<>();
        menuListView.setPrefHeight(280);
        menuListView.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        categoryCombo.setOnAction(e -> {
            MenuCategory selected = categoryCombo.getValue();
            if (selected != null) {
                menuListView.getItems().clear();
                for (MenuComponent comp : selected.getComponents()) {
                    if (comp instanceof MenuIt) {
                        menuListView.getItems().add((MenuIt) comp);
                    }
                }
            }
        });

        categoryCombo.getItems().addAll(controller.getMenuCategories());

        // Zone quantité simplifiée
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        Label qtyLabel = new Label("Quantité:");
        qtyLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));

        quantityField = new TextField("1");
        quantityField.setPrefWidth(60);
        quantityField.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-alignment: center; -fx-background-radius: 5;");

        Button minusBtn = new Button("−");
        minusBtn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        minusBtn.setPrefWidth(35);
        minusBtn.setOnAction(e -> {
            int val = Integer.parseInt(quantityField.getText());
            if (val > 1) quantityField.setText(String.valueOf(val - 1));
        });

        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        plusBtn.setPrefWidth(35);
        plusBtn.setOnAction(e -> {
            int val = Integer.parseInt(quantityField.getText());
            if (val < 20) quantityField.setText(String.valueOf(val + 1));
        });

        quantityBox.getChildren().addAll(qtyLabel, minusBtn, quantityField, plusBtn);

        Button addButton = new Button("➕ Ajouter à la commande");
        addButton.setPrefWidth(370);
        addButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        addButton.setOnAction(e -> {
            MenuIt selected = menuListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int quantity = Integer.parseInt(quantityField.getText());
                controller.addItemToOrder(selected, quantity);
                updateOrderDisplay();
                logAction("Ajouté: " + quantity + "x " + selected.getName());
            } else {
                showAlert("Veuillez sélectionner un plat");
            }
        });

        panel.getChildren().addAll(titleLabel, categoryCombo, menuListView, quantityBox, addButton);
        return panel;
    }

    private VBox createOrderPanel() {
        VBox panel = new VBox(12);
        panel.setPrefWidth(350);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label titleLabel = new Label("🛒 COMMANDE EN COURS");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        orderListView = new ListView<>();
        orderListView.setPrefHeight(280);
        orderListView.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        totalLabel = new Label("Total: 0.00 DA");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        totalLabel.setTextFill(Color.web("#e74c3c"));

        Button removeButton = new Button("🗑️ Retirer l'article");
        removeButton.setPrefWidth(320);
        removeButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        removeButton.setOnAction(e -> {
            int selectedIndex = orderListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                controller.removeItemFromOrder(selectedIndex);
                updateOrderDisplay();
                logAction("Article retiré de la commande");
            } else {
                showAlert("Veuillez sélectionner un article à retirer");
            }
        });

        Button newOrderButton = new Button("🆕 Nouvelle Commande");
        newOrderButton.setPrefWidth(320);
        newOrderButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        newOrderButton.setOnAction(e -> {
            controller.createNewOrder();
            updateOrderDisplay();
            logAction("Nouvelle commande créée");
        });

        panel.getChildren().addAll(titleLabel, orderListView, totalLabel, removeButton, newOrderButton);
        return panel;
    }

    private VBox createPaymentPanel() {
        VBox panel = new VBox(12);
        panel.setPrefWidth(350);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label titleLabel = new Label("💳 PAIEMENT");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("Espèces", "Payer par carte", "Payer sur place");
        paymentMethodCombo.setPromptText("Méthode de paiement");
        paymentMethodCombo.setPrefWidth(320);
        paymentMethodCombo.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        // Container pour les champs dynamiques
        paymentFieldsContainer = new VBox(8);
        paymentFieldsContainer.setPrefWidth(320);

        paymentMethodCombo.setOnAction(e -> {
            String method = paymentMethodCombo.getValue();
            updatePaymentFields(method);
        });

        Button payButton = new Button("✅ VALIDER");
        payButton.setPrefWidth(320);
        payButton.setPrefHeight(45);
        payButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        payButton.setOnAction(e -> processPayment());

        Button cancelButton = new Button("❌ Annuler la commande");
        cancelButton.setPrefWidth(320);
        cancelButton.setStyle("-fx-font-size: 13px; " +
                "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> cancelOrder());

        panel.getChildren().addAll(
                titleLabel,
                paymentMethodCombo,
                paymentFieldsContainer,
                payButton,
                cancelButton
        );

        return panel;
    }

    private void updatePaymentFields(String method) {
        paymentFieldsContainer.getChildren().clear();

        if ("Espèces".equals(method)) {
            // Nom
            TextField nomField = new TextField();
            nomField.setPromptText("Nom");
            nomField.setId("nom");
            nomField.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

            // Prénom
            TextField prenomField = new TextField();
            prenomField.setPromptText("Prénom");
            prenomField.setId("prenom");
            prenomField.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

            // Téléphone
            TextField telField = new TextField();
            telField.setPromptText("Numéro de téléphone");
            telField.setId("telephone");
            telField.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

            // Adresse
            TextField adresseField = new TextField();
            adresseField.setPromptText("Adresse");
            adresseField.setId("adresse");
            adresseField.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

            paymentFieldsContainer.getChildren().addAll(nomField, prenomField, telField, adresseField);

        } else if ("Payer par carte".equals(method)) {
            // Numéro de carte
            TextField cardNumberField = new TextField();
            cardNumberField.setPromptText("Numéro de carte");
            cardNumberField.setId("cardNumber");
            cardNumberField.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

            // Date d'expiration
            TextField expiryField = new TextField();
            expiryField.setPromptText("Date d'expiration (MM/AA)");
            expiryField.setId("expiry");
            expiryField.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

            // Nom sur la carte
            TextField nameField = new TextField();
            nameField.setPromptText("Nom sur la carte");
            nameField.setId("cardName");
            nameField.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

            // Code CVC
            TextField cvcField = new TextField();
            cvcField.setPromptText("Code CVC2/CVV2");
            cvcField.setId("cvc");
            cvcField.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

            paymentFieldsContainer.getChildren().addAll(cardNumberField, expiryField, nameField, cvcField);
        }
        // Pour "Payer sur place" on n'ajoute rien (pas de champs)
    }

    private void cancelOrder() {
        if (!controller.hasCurrentOrder()) {
            showAlert("Aucune commande à annuler");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Annuler la commande ?");
        confirm.setContentText("Voulez-vous vraiment annuler cette commande ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.createNewOrder(); // Réinitialise la commande
                updateOrderDisplay();
                paymentMethodCombo.setValue(null);
                paymentFieldsContainer.getChildren().clear();
                logAction("Commande annulée");
                showSuccessAlert("La commande a été annulée avec succès");
            }
        });
    }

    private VBox createConsoleOutput() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label label = new Label("📊 Journal d'activité");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.web("#2c3e50"));

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(100);
        outputArea.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; " +
                "-fx-font-size: 12px; -fx-background-radius: 5;");

        panel.getChildren().addAll(label, outputArea);
        return panel;
    }

    private void loadMenu() {
        logAction("Système initialisé");
        logAction("Menu chargé avec structure hiérarchique");
    }

    private void updateOrderDisplay() {
        orderListView.getItems().clear();

        Order currentOrder = controller.getCurrentOrder();
        if (currentOrder != null) {
            orderListView.getItems().addAll(currentOrder.getItems());
            totalLabel.setText(String.format("Total: %.2f DA", currentOrder.getTotal()));
        } else {
            totalLabel.setText("Total: 0.00 DA");
        }
    }

    private void processPayment() {
        if (!controller.hasCurrentOrder()) {
            showAlert("Aucune commande en cours");
            return;
        }

        String method = paymentMethodCombo.getValue();
        if (method == null) {
            showAlert("Veuillez sélectionner une méthode de paiement");
            return;
        }

        String methodCode = "";
        String details = "";

        if ("Espèces".equals(method)) {
            // Vérifier les champs espèces
            TextField nomField = (TextField) paymentFieldsContainer.lookup("#nom");
            TextField prenomField = (TextField) paymentFieldsContainer.lookup("#prenom");
            TextField telField = (TextField) paymentFieldsContainer.lookup("#telephone");
            TextField adresseField = (TextField) paymentFieldsContainer.lookup("#adresse");

            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
                    telField.getText().isEmpty() || adresseField.getText().isEmpty()) {
                showAlert("Veuillez remplir tous les champs");
                return;
            }

            methodCode = "CASH";
            details = nomField.getText() + " " + prenomField.getText();

        } else if ("Payer par carte".equals(method)) {
            // Vérifier les champs carte
            TextField cardNumberField = (TextField) paymentFieldsContainer.lookup("#cardNumber");
            TextField expiryField = (TextField) paymentFieldsContainer.lookup("#expiry");
            TextField nameField = (TextField) paymentFieldsContainer.lookup("#cardName");
            TextField cvcField = (TextField) paymentFieldsContainer.lookup("#cvc");

            if (cardNumberField.getText().isEmpty() || expiryField.getText().isEmpty() ||
                    nameField.getText().isEmpty() || cvcField.getText().isEmpty()) {
                showAlert("Veuillez remplir tous les champs");
                return;
            }

            methodCode = "CARD";
            details = cardNumberField.getText();

        } else if ("Payer sur place".equals(method)) {
            methodCode = "ONSITE";
            details = "";
        }

        controller.setPaymentMethod(methodCode, details);

        if (controller.validateAndPayOrder()) {
            logAction("=== PAIEMENT RÉUSSI ===");
            logAction("Méthode: " + method);
            logAction("Notification envoyée à la cuisine");
            logAction("========================");

            updateOrderDisplay();
            paymentMethodCombo.setValue(null);
            paymentFieldsContainer.getChildren().clear();

            showSuccessAlert("Commande validée et payée avec succès!\nLa cuisine a été notifiée.");
        } else {
            showAlert("Échec du paiement");
        }
    }

    private void logAction(String message) {
        outputArea.appendText(message + "\n");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}