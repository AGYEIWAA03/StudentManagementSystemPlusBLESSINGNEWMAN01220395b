package controller;

import domain.Student;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.ReportService;
import util.SettingsManager;
import util.UIRefresh;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    // Charts
    @FXML private PieChart gpaChart;
    @FXML private BarChart<String, Number> programmeChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    // Risk Table
    @FXML private TableView<Student> riskTable;
    @FXML private TableColumn<Student, String> colRiskId;
    @FXML private TableColumn<Student, String> colRiskName;
    @FXML private TableColumn<Student, String> colRiskProgramme;
    @FXML private TableColumn<Student, Double> colRiskGpa;
    @FXML private Label lblRiskThreshold;

    // Top Performers Table
    @FXML private TableView<Student> topTable;
    @FXML private TableColumn<Student, String> colTopId;
    @FXML private TableColumn<Student, String> colTopName;
    @FXML private TableColumn<Student, String> colTopProgramme;
    @FXML private TableColumn<Student, Double> colTopGpa;

    private final ReportService reportService = new ReportService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTables();
        loadAllReports();

        // Refresh automatically if data changes elsewhere
        UIRefresh.subscribe(this::loadAllReports);
    }

    private void setupTables() {
        // Setup Risk Table Columns
        colRiskId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colRiskName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colRiskProgramme.setCellValueFactory(new PropertyValueFactory<>("programme"));
        colRiskGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));

        // Setup Top Performers Table Columns
        colTopId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colTopName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colTopProgramme.setCellValueFactory(new PropertyValueFactory<>("programme"));
        colTopGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
    }

    private void loadAllReports() {
        loadGpaChart();
        loadProgrammeChart();
        loadRiskTable();
        loadTopPerformers();
    }

    private void loadGpaChart() {
        Map<String, Number> data = reportService.getGpaDistribution();
        gpaChart.getData().clear();

        data.forEach((key, value) -> {
            if (value.intValue() > 0) {
                gpaChart.getData().add(new PieChart.Data(key + " (" + value + ")", value.intValue()));
            }
        });
    }

    private void loadProgrammeChart() {
        Map<String, Number> data = reportService.getProgrammeAverageGpa();
        programmeChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Avg GPA");

        data.forEach((programme, avgGpa) -> {
            // Truncate long names for display
            String displayName = programme.length() > 10 ? programme.substring(0, 10) + "." : programme;
            series.getData().add(new XYChart.Data<>(displayName, avgGpa));
        });

        programmeChart.getData().add(series);
    }

    private void loadRiskTable() {
        // Get threshold from settings (default 2.0)
        String thresholdStr = SettingsManager.getSetting("riskThreshold", "2.0");
        lblRiskThreshold.setText(thresholdStr);

        double threshold = Double.parseDouble(thresholdStr);
        List<Student> riskStudents = reportService.getAtRiskStudents(threshold);
        riskTable.setItems(FXCollections.observableArrayList(riskStudents));
    }

    private void loadTopPerformers() {
        // Get top 10 students
        List<Student> topStudents = reportService.getTopPerformers("All", 10);
        topTable.setItems(FXCollections.observableArrayList(topStudents));
    }

    // --- TITLE BAR BUTTON LOGIC ---

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
}