package com.restaurant.view;

import com.restaurant.model.User;
import com.restaurant.model.UserManager;
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
 * VUE - Inscription avec sélection de rôle
 */
public class RegisterView {
    private Stage stage;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private ComboBox<String> roleCombo;

    public RegisterView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FF6B35, #F7931E);");

        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(450);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");

        Label title = new Label("📝 CRÉER UN COMPTE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));

        // Rôle
        VBox roleBox = new VBox(8);
        Label roleLabel = new Label("Type de compte");
        roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("👤 Client", "🚚 Livreur", "👨‍💼 Admin");
        roleCombo.setValue("👤 Client");
        roleCombo.setPrefHeight(40);
        roleCombo.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");
        roleBox.getChildren().addAll(roleLabel, roleCombo);

        // Info rôle
        Label roleInfo = new Label("ℹ️ Clients : accès immédiat\nLivreurs et Admins : validation requise");
        roleInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        roleInfo.setTextFill(Color.web("#7f8c8d"));
        roleInfo.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 8; -fx-background-radius: 5;");

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

        formBox.getChildren().addAll(
                title,
                roleBox,
                roleInfo,
                emailBox,
                passwordBox,
                confirmBox,
                buttonsBox
        );

        root.getChildren().add(formBox);

        Scene scene = new Scene(root, 800, 650);
        stage.setTitle("Inscription");
        stage.setScene(scene);
        stage.show();
    }

    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String roleStr = roleCombo.getValue();

        // Validations
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        if (!UserManager.isValidEmail(email)) {
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

        // Déterminer le rôle
        User.UserRole role;
        if (roleStr.contains("Client")) {
            role = User.UserRole.CLIENT;
        } else if (roleStr.contains("Livreur")) {
            role = User.UserRole.LIVREUR;
        } else {
            role = User.UserRole.ADMIN;
        }

        // Enregistrement
        UserManager userManager = UserManager.getInstance();

        if (userManager.emailExists(email)) {
            showAlert("Erreur", "Cet email est déjà enregistré", Alert.AlertType.ERROR);
            return;
        }

        if (userManager.registerUser(email, password, role)) {
            if (role == User.UserRole.CLIENT) {
                showAlert("Succès",
                        "Compte client créé avec succès !\nVous pouvez maintenant vous connecter.",
                        Alert.AlertType.INFORMATION);
            } else {
                showAlert("Demande envoyée",
                        "Votre demande de compte " + roleStr + " a été envoyée.\n" +
                                "Un administrateur doit l'approuver avant que vous puissiez vous connecter.",
                        Alert.AlertType.INFORMATION);
            }
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