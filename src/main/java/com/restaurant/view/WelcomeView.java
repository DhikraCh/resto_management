package com.restaurant.view;

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
 * VUE - Page d'accueil (Bienvenue)
 * Permet de choisir entre Admin ou Invité
 */
public class WelcomeView {
    private Stage stage;

    public WelcomeView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Vérifier si un admin est déjà connecté
        if (com.restaurant.model.UserSession.getInstance().isAdmin()) {
            // Rediriger automatiquement vers AdminView
            System.out.println("🔄 Admin déjà connecté, redirection vers dashboard...");
            new AdminView(stage).show();
            return;
        }

        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FF6B35, #F7931E);");

        // Logo et titre
        Label logo = new Label("🍽️");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 80));

        Label title = new Label("SYSTÈME DE RESTAURANT");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Bienvenue ! Choisissez votre mode d'accès");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web("#fff8dc"));

        // Container pour les boutons
        VBox buttonsBox = new VBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(30));
        buttonsBox.setMaxWidth(400);
        buttonsBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");

        // Bouton Admin
        Button adminButton = new Button("👨‍💼 ESPACE ADMINISTRATEUR");
        adminButton.setPrefWidth(350);
        adminButton.setPrefHeight(60);
        adminButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #2c3e50; -fx-text-fill: white; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        adminButton.setOnAction(e -> showAdminOptions());

        // Bouton Invité
        Button guestButton = new Button("🛒 CONTINUER EN TANT QU'INVITÉ");
        guestButton.setPrefWidth(350);
        guestButton.setPrefHeight(60);
        guestButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        guestButton.setOnAction(e -> loginAsGuest());

        Label infoLabel = new Label("💡 Les invités peuvent passer des commandes\n" +
                "Les admins ont accès aux statistiques");
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        infoLabel.setTextFill(Color.web("#7f8c8d"));
        infoLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        buttonsBox.getChildren().addAll(adminButton, guestButton, infoLabel);

        root.getChildren().addAll(logo, title, subtitle, buttonsBox);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Restaurant - Bienvenue");
        stage.setScene(scene);
        stage.show();
    }

    private void showAdminOptions() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Espace Administrateur");
        alert.setHeaderText("Choisissez une option");
        alert.setContentText("Avez-vous déjà un compte administrateur ?");

        ButtonType loginBtn = new ButtonType("Se connecter");
        ButtonType registerBtn = new ButtonType("Créer un compte");
        ButtonType cancelBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(loginBtn, registerBtn, cancelBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == loginBtn) {
                new LoginView(stage).show();
            } else if (response == registerBtn) {
                new RegisterView(stage).show();
            }
        });
    }

    private void loginAsGuest() {
        com.restaurant.model.UserSession.getInstance().loginAsGuest();

        com.restaurant.controller.RestaurantController controller =
                new com.restaurant.controller.RestaurantController();
        new RestaurantView(controller, stage).show();
    }
}