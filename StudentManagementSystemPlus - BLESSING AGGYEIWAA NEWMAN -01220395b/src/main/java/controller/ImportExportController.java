package controller;

import service.ImportExportService;
import service.StudentService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class ImportExportController {
    @FXML private Label lblImportStatus;
    @FXML private Label lblExportStatus; // Added

    private ImportExportService importExportService = new ImportExportService();
    private StudentService studentService = new StudentService();

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
                    return importExportService.importFromCSV(file.getAbsolutePath(), studentService);
                }
            };

            task.setOnSucceeded(e -> {
                int[] res = task.getValue();
                lblImportStatus.setText("Done! Success: " + res[0] + " Failed: " + res[1]);
            });

            task.setOnFailed(e -> lblImportStatus.setText("Error during import."));

            new Thread(task).start();
        }
    }

    // ADDED: Export All Logic
    @FXML
    private void handleExportAll() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("students_export.csv");
        File file = fc.showSaveDialog(new Stage());

        if (file != null) {
            try {
                importExportService.exportToCSV(studentService.getAllStudents(), file.getAbsolutePath());
                lblExportStatus.setText("Exported successfully!");
            } catch (Exception e) {
                lblExportStatus.setText("Export failed.");
            }
        }
    }
}