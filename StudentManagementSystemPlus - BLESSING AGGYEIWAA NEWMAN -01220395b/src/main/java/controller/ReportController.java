package controller;

import domain.Student;
import service.ImportExportService;
import service.ReportService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ReportController {
    @FXML private TableView<Student> tableReports;
    @FXML private TableColumn<Student, String> colRId, colRName, colRGpa;

    @FXML private TableView<Map.Entry<String, Number>> tableSummary;
    @FXML private TableColumn<Map.Entry<String, Number>, String> colKey;
    @FXML private TableColumn<Map.Entry<String, Number>, String> colValue;

    @FXML private ComboBox<String> cbReportType;
    @FXML private TextField tfGpaThreshold;
    @FXML private Label lblSummary;

    private ReportService reportService = new ReportService();
    private ImportExportService exportService = new ImportExportService();

    @FXML
    public void initialize() {
        try {
            cbReportType.getItems().addAll("Top Performers", "At Risk Students", "GPA Distribution", "Programme Summary");
            cbReportType.setValue("Top Performers");

            colRId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentId()));
            colRName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
            colRGpa.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f", d.getValue().getGpa())));

            colKey.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getKey()));
            colValue.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f", d.getValue().getValue().doubleValue())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void generateReport() {
        String type = cbReportType.getValue();
        boolean isStudent = type.equals("Top Performers") || type.equals("At Risk Students");

        tableReports.setVisible(isStudent);
        tableReports.setManaged(isStudent);
        tableSummary.setVisible(!isStudent);
        tableSummary.setManaged(!isStudent);

        try {
            if (isStudent) {
                List<Student> list;
                if (type.equals("Top Performers")) {
                    list = reportService.getTopPerformers("All", 10);
                } else {
                    double t = Double.parseDouble(tfGpaThreshold.getText());
                    list = reportService.getAtRiskStudents(t);
                }
                tableReports.setItems(FXCollections.observableArrayList(list));
            } else {
                Map<String, Number> data = type.equals("GPA Distribution") ? reportService.getGpaDistribution() : reportService.getProgrammeAverageGpa();
                tableSummary.setItems(FXCollections.observableArrayList(data.entrySet()));
            }
        } catch (Exception e) {
            lblSummary.setText("Error: " + e.getMessage());
        }
    }

    // FIXED: Implemented Export Logic
    @FXML
    private void exportReport() {
        try {
            new File("data").mkdirs();
            String filename = "data/report_" + System.currentTimeMillis() + ".csv";

            if (tableReports.isVisible()) {
                // Export Student List
                exportService.exportToCSV(tableReports.getItems(), filename);
            } else {
                // Export Summary Map
                try (PrintWriter writer = new PrintWriter(new File(filename))) {
                    writer.println("Category,Value");
                    for (Map.Entry<String, Number> entry : tableSummary.getItems()) {
                        writer.println(entry.getKey() + "," + entry.getValue());
                    }
                }
            }
            lblSummary.setText("Exported to " + filename);
        } catch (Exception e) {
            lblSummary.setText("Export failed: " + e.getMessage());
        }
    }
}