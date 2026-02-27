package controller;

import domain.Student;
import service.ReportService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private Label lblTotalStudents;
    @FXML private Label lblActiveStudents;
    @FXML private Label lblInactiveStudents;
    @FXML private Label lblAvgGpa;
    @FXML private HBox titleBar;

    private ReportService reportService = new ReportService();
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDashboardData();
        setupDraggableWindow();

        // NEW: Refresh data whenever the window gains focus
        // This ensures stats update immediately after closing a popup
        titleBar.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsW, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        ((Stage) newWindow).focusedProperty().addListener((obsF, wasFocused, isNowFocused) -> {
                            if (isNowFocused) {
                                loadDashboardData();
                            }
                        });
                    }
                });
            }
        });
    }

    private void loadDashboardData() {
        ObservableList<Student> allStudents = reportService.getAllStudents();
        int total = allStudents.size();
        long active = reportService.getActiveCount();
        long inactive = total - active;
        double avgGpa = reportService.getAverageGpa();

        lblTotalStudents.setText(String.valueOf(total));
        lblActiveStudents.setText(String.valueOf(active));
        lblInactiveStudents.setText(String.valueOf(inactive));
        lblAvgGpa.setText(String.format("%.2f", avgGpa));
    }

    // --- Window Controls ---

    private void setupDraggableWindow() {
        if (titleBar != null) {
            titleBar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            titleBar.setOnMouseDragged(event -> {
                Stage stage = (Stage) titleBar.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        }
    }

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) lblTotalStudents.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = (Stage) lblTotalStudents.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) lblTotalStudents.getScene().getWindow();
        stage.close();
    }

    // --- Navigation ---

    @FXML
    private void openStudentsWindow() {
        loadWindow("/fxml/Student.fxml", "Student Management", 900, -1);
    }

    @FXML
    private void openReportsWindow() {
        loadWindow("/fxml/Report.fxml", "Reports", 900, 500);
    }

    @FXML
    private void openImportWindow() {
        loadWindow("/fxml/ImportExport.fxml", "Import/Export", 600, 400);
    }

    @FXML
    private void openSettingsWindow() {
        loadWindow("/fxml/Settings.fxml", "Settings", 500, 300);
    }

    private void loadWindow(String fxmlPath, String title, double width, double height) {
        try {
            if (getClass().getResource(fxmlPath) == null) {
                showError("File Not Found", "Could not find: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);

            Scene scene;
            if (width > 0 && height > 0) {
                scene = new Scene(root, width, height);
            } else if (width > 0) {
                scene = new Scene(root, width, root.prefHeight(-1));
            } else {
                scene = new Scene(root);
            }

            stage.setScene(scene);

            // Position to the right
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double w = (width > 0) ? width : 800;
            stage.setX(screenBounds.getMaxX() - w - 20);
            stage.setY(50);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to open window: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}