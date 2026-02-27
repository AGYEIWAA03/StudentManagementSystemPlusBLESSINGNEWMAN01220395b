package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;

public class LoginController {
    @FXML private TextField tfUsername;
    @FXML private PasswordField tfPassword;
    @FXML private Label lblError;

    @FXML
    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = tfPassword.getText();

        // Reset error visibility
        lblError.setVisible(false);

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter username and password.");
            lblError.setVisible(true);
            return;
        }

        if (username.equals("admin") && password.equals("ATU")) {
            boolean success = openDashboard();
            if (success) {
                Stage stage = (Stage) tfUsername.getScene().getWindow();
                stage.close();
            }
        } else {
            lblError.setText("Invalid username or password.");
            lblError.setVisible(true);
        }
    }

    private boolean openDashboard() {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/Dashboard.fxml");
            if (fxmlLocation == null) {
                showErrorAlert("File Missing", "Dashboard.fxml not found!");
                return false;
            }

            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("AJ Management System");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load Dashboard: " + e.getMessage());
            return false;
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}