package controller;

import domain.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle; // ADDED IMPORT
import service.StudentService;
import util.UIRefresh;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblTotalStudents;
    @FXML private Label lblActiveStudents;
    @FXML private Label lblInactiveStudents;
    @FXML private Label lblAvgGpa;

    private final StudentService studentService = new StudentService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStats();
        UIRefresh.subscribe(this::loadStats);
    }

    private void loadStats() {
        List<Student> students = studentService.getAllStudents();

        long total = students.size();
        long active = students.stream().filter(s -> "Active".equalsIgnoreCase(s.getStatus())).count();
        long inactive = total - active;
        double avgGpa = students.stream().mapToDouble(Student::getGpa).average().orElse(0.0);

        lblTotalStudents.setText(String.valueOf(total));
        lblActiveStudents.setText(String.valueOf(active));
        lblInactiveStudents.setText(String.valueOf(inactive));
        lblAvgGpa.setText(String.format("%.2f", avgGpa));
    }

    // --- NAVIGATION LOGIC ---

    @FXML
    private void openStudentsWindow() {
        openWindow("/view/Student.fxml", "Students");
    }

    @FXML
    private void openReportsWindow() {
        openWindow("/view/Report.fxml", "Reports");
    }

    @FXML
    private void openImportWindow() {
        openWindow("/view/ImportExport.fxml", "Import / Export");
    }

    @FXML
    private void openSettingsWindow() {
        openWindow("/view/Settings.fxml", "Settings");
    }

    // --- TITLE BAR LOGIC (FIXED) ---

    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // --- HELPER METHOD ---

    private void openWindow(String fxmlPath, String title) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);

            if (fxmlLocation == null) {
                fxmlLocation = getClass().getClassLoader().getResource(fxmlPath.substring(1));
            }

            if (fxmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "File Missing", "Could not find file: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = new Stage();

            // FIX: This removes the default Windows title bar
            stage.initStyle(StageStyle.UNDECORATED);

            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open " + title + ": " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}