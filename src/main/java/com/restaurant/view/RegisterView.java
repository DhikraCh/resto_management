package com.restaurant.view;

import com.restaurant.model.AdminManager;
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
 * VUE - Inscription administrateur
 */
public class RegisterView {
    private Stage stage;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    public RegisterView(Stage stage) {
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
        Label title = new Label("📝 CRÉER UN COMPTE ADMIN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));

        // Email
        VBox emailBox = new VBox(8);
        Label emailLabel = new Label("Email (format: exemple@gmail.com)");
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

        // Confirm Password
        VBox confirmBox = new VBox(8);
        Label confirmLabel = new Label("Confirmer le mot de passe");
        confirmLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmez votre mot de passe");
        confirmPasswordField.setPrefHeight(40);
        confirmPasswordField.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");
        confirmBox.getChildren().addAll(confirmLabel, confirmPasswordField);

        // Boutons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        Button registerButton = new Button("✅ S'INSCRIRE");
        registerButton.setPrefWidth(180);
        registerButton.setPrefHeight(45);
        registerButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        registerButton.setOnAction(e -> handleRegister());

        Button backButton = new Button("← Retour");
        backButton.setPrefWidth(180);
        backButton.setPrefHeight(45);
        backButton.setStyle("-fx-font-size: 14px; " +
                "-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-cursor: hand;");
        backButton.setOnAction(e -> new WelcomeView(stage).show());

        buttonsBox.getChildren().addAll(backButton, registerButton);

        // Info
        Label infoLabel = new Label("⚠️ Conservez bien vos identifiants");
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        infoLabel.setTextFill(Color.web("#e74c3c"));

        formBox.getChildren().addAll(
                title,
                emailBox,
                passwordBox,
                confirmBox,
                buttonsBox,
                infoLabel
        );

        root.getChildren().add(formBox);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Inscription Administrateur");
        stage.setScene(scene);
        stage.show();
    }

    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validations
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        if (!AdminManager.isValidEmail(email)) {
            showAlert("Erreur", "Format d'email invalide\nUtilisez: exemple@gmail.com", Alert.AlertType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas", Alert.AlertType.ERROR);
            return;
        }

        if (password.length() < 6) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères", Alert.AlertType.ERROR);
            return;
        }

        // Enregistrement
        AdminManager adminManager = AdminManager.getInstance();

        if (adminManager.emailExists(email)) {
            showAlert("Erreur", "Cet email est déjà enregistré", Alert.AlertType.ERROR);
            return;
        }

        if (adminManager.registerAdmin(email, password)) {
            showAlert("Succès", "Compte créé avec succès !\nVous pouvez maintenant vous connecter", Alert.AlertType.INFORMATION);
            new LoginView(stage).show();
        } else {
            showAlert("Erreur", "Erreur lors de la création du compte", Alert.AlertType.ERROR);
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