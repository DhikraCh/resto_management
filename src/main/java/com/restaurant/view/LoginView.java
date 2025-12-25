package com.restaurant.view;

import com.restaurant.model.AdminManager;
import com.restaurant.model.UserSession;
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
 * VUE - Connexion administrateur
 */
public class LoginView {
    private Stage stage;
    private TextField emailField;
    private PasswordField passwordField;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FF6B35, #F7931E);");

        // Container principal
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(450);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");

        // Titre
        Label title = new Label("🔐 CONNEXION ADMIN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));

        // Email
        VBox emailBox = new VBox(8);
        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        emailField = new TextField();
        emailField.setPromptText("votre-email@gmail.com");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");
        emailBox.getChildren().addAll(emailLabel, emailField);

        // Password
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Boutons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("✅ SE CONNECTER");
        loginButton.setPrefWidth(180);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        loginButton.setOnAction(e -> handleLogin());

        Button backButton = new Button("← Retour");
        backButton.setPrefWidth(180);
        backButton.setPrefHeight(45);
        backButton.setStyle("-fx-font-size: 14px; " +
                "-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        backButton.setOnAction(e -> new WelcomeView(stage).show());

        buttonsBox.getChildren().addAll(backButton, loginButton);

        // Lien inscription
        Hyperlink registerLink = new Hyperlink("Pas encore de compte ? Créer un compte");
        registerLink.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        registerLink.setOnAction(e -> new RegisterView(stage).show());

        formBox.getChildren().addAll(
                title,
                emailBox,
                passwordBox,
                buttonsBox,
                registerLink
        );

        root.getChildren().add(formBox);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Connexion Administrateur");
        stage.setScene(scene);
        stage.show();
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validations
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        // Vérification
        AdminManager adminManager = AdminManager.getInstance();

        if (adminManager.login(email, password)) {
            UserSession.getInstance().loginAsAdmin(email);
            showAlert("Succès", "Connexion réussie !\nBienvenue " + email, Alert.AlertType.INFORMATION);
            new AdminView(stage).show();
        } else {
            showAlert("Erreur", "Email ou mot de passe incorrect", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}