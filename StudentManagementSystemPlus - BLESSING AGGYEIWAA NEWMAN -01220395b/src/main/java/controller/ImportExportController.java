package controller;

import domain.Student;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.ImportExportService;
import service.StudentService;
import util.UIRefresh;

import java.io.File;
import java.util.List;

public class ImportExportController {

    @FXML private Label lblImportStatus;
    @FXML private Label lblExportStatus;

    private final ImportExportService importExportService = new ImportExportService();
    private final StudentService studentService = new StudentService();

    @FXML
    private void handleImport() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fc.showOpenDialog(new Stage());

        if (file != null) {
            lblImportStatus.setText("Importing...");

            Task<int[]> task = new Task<int[]>() {
                @Override
                protected int[] call() {
                    return importExportService.importFromCSV(file.getAbsolutePath());
                }
            };

            task.setOnSucceeded(e -> {
                int[] res = task.getValue();

                // TRIGGER THE REFRESH!
                UIRefresh.notifyDataChanged();

                showAlert(Alert.AlertType.INFORMATION, "Import Finished",
                        "Success: " + res[0] + "\nFailed: " + res[1]);
                lblImportStatus.setText("Done.");
            });

            task.setOnFailed(e -> {
                lblImportStatus.setText("Error.");
                showAlert(Alert.AlertType.ERROR, "Error", "Import failed.");
            });

            new Thread(task).start();
        }
    }

    @FXML
    private void handleExportAll() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("students_export.csv");
        File file = fc.showSaveDialog(new Stage());

        if (file != null) {
            try {
                List<Student> students = studentService.getAllStudents();
                importExportService.exportToCSV(students, file.getAbsolutePath());
                lblExportStatus.setText("Exported successfully!");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Data exported.");
            } catch (Exception e) {
                lblExportStatus.setText("Export failed.");
                showAlert(Alert.AlertType.ERROR, "Error", "Export failed.");
            }
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