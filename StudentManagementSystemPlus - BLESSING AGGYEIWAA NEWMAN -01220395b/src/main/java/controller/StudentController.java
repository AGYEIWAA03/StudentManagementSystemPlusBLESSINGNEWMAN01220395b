package controller;

import domain.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import service.StudentService;
import util.UIRefresh;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    // TABLE COLUMNS
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colProgramme;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colPhone;
    @FXML private TableColumn<Student, String> colDate;
    @FXML private TableColumn<Student, Integer> colLevel;
    @FXML private TableColumn<Student, Double> colGpa;
    @FXML private TableColumn<Student, String> colStatus;

    // FILTERS
    @FXML private TextField tfSearch;
    @FXML private ComboBox<Integer> cbFilterLevel;
    @FXML private ComboBox<String> cbFilterProgramme;
    @FXML private ComboBox<String> cbFilterStatus;

    // FORM INPUTS
    @FXML private TextField tfId;
    @FXML private TextField tfName;
    @FXML private TextField tfProgramme;
    @FXML private ComboBox<Integer> cbLevel;
    @FXML private TextField tfGpa;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Label lblMessage;

    private final StudentService studentService = new StudentService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        populateComboBoxes();
        loadTable();

        // FIX: Add listener to table selection to populate form for updating
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        UIRefresh.subscribe(this::loadTable);
    }

    // NEW: Helper to fill form when row is clicked
    private void populateForm(Student s) {
        tfId.setText(s.getStudentId());
        tfId.setEditable(false); // Prevent changing ID during update
        tfName.setText(s.getFullName());
        tfProgramme.setText(s.getProgramme());
        cbLevel.setValue(s.getLevel());
        tfGpa.setText(String.valueOf(s.getGpa()));
        tfEmail.setText(s.getEmail());
        tfPhone.setText(s.getPhone());
        dpDate.setValue(s.getDate());
        cbStatus.setValue(s.getStatus());
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colProgramme.setCellValueFactory(new PropertyValueFactory<>("programme"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void populateComboBoxes() {
        cbLevel.getItems().addAll(100, 200, 300, 400, 500, 600, 700);
        cbStatus.getItems().addAll("Active", "Inactive");

        cbFilterProgramme.getItems().add(null);
        cbFilterProgramme.getItems().addAll("BUSINESS", "ENGINEERING", "IT", "ACCOUNTANCY", "HOTEL MANAGEMENT");

        cbFilterLevel.getItems().add(null);
        cbFilterLevel.getItems().addAll(100, 200, 300, 400, 500, 600, 700);

        cbFilterStatus.getItems().add(null);
        cbFilterStatus.getItems().addAll("Active", "Inactive");
    }

    private void loadTable() {
        List<Student> list = studentService.getAllStudents();
        ObservableList<Student> data = FXCollections.observableArrayList(list);
        studentTable.setItems(data);
    }

    @FXML
    private void handleAdd() {
        try {
            String id = tfId.getText();
            String name = tfName.getText();
            String programme = tfProgramme.getText();
            Integer level = cbLevel.getValue();
            String gpaText = tfGpa.getText();
            String email = tfEmail.getText();
            String phone = tfPhone.getText();
            LocalDate date = dpDate.getValue();
            String status = cbStatus.getValue();

            if (id.isEmpty() || name.isEmpty() || level == null || status == null) {
                lblMessage.setText("Error: ID, Name, Level, and Status are required.");
                return;
            }

            double gpa = gpaText.isEmpty() ? 0.0 : Double.parseDouble(gpaText);

            Student s = new Student(id, name, programme, level, gpa, email, phone, date, status);
            studentService.addStudent(s);

            lblMessage.setText("Student added successfully!");
            clearForm();
            loadTable();
            UIRefresh.notifyDataChanged();

        } catch (NumberFormatException e) {
            lblMessage.setText("Error: GPA must be a number.");
        } catch (Exception e) {
            lblMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            Student selected = studentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                lblMessage.setText("Select a student to update.");
                return;
            }

            // Update the selected object's data
            selected.setFullName(tfName.getText());
            selected.setProgramme(tfProgramme.getText());
            selected.setLevel(cbLevel.getValue());

            String gpaText = tfGpa.getText();
            double gpa = gpaText.isEmpty() ? 0.0 : Double.parseDouble(gpaText);
            selected.setGpa(gpa);

            selected.setEmail(tfEmail.getText());
            selected.setPhone(tfPhone.getText());
            selected.setDate(dpDate.getValue());
            selected.setStatus(cbStatus.getValue());

            studentService.updateStudent(selected);
            lblMessage.setText("Student updated successfully!");

            loadTable();
            UIRefresh.notifyDataChanged();

        } catch (NumberFormatException e) {
            lblMessage.setText("Error: GPA must be a number.");
        } catch (Exception e) {
            lblMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblMessage.setText("Select a student to delete.");
            return;
        }

        try {
            studentService.deleteStudent(selected.getStudentId());
            lblMessage.setText("Student deleted successfully!");
            clearForm();
            loadTable();
            UIRefresh.notifyDataChanged();
        } catch (Exception e) {
            lblMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = tfSearch.getText();
        if (query.isEmpty()) {
            loadTable();
        } else {
            List<Student> results = studentService.searchStudents(query);
            studentTable.setItems(FXCollections.observableArrayList(results));
        }
    }

    @FXML
    private void handleRefresh() {
        tfSearch.clear();
        cbFilterProgramme.setValue(null);
        cbFilterLevel.setValue(null);
        cbFilterStatus.setValue(null);
        loadTable();
    }

    @FXML
    private void handleFilter() {
        String prog = cbFilterProgramme.getValue();
        Integer lvl = cbFilterLevel.getValue();
        String stat = cbFilterStatus.getValue();
        List<Student> filtered = studentService.getFilteredStudents(prog, lvl, stat);
        studentTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleSortGPA() {
        List<Student> list = studentService.getAllStudents();
        list.sort((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()));
        studentTable.setItems(FXCollections.observableArrayList(list));
    }

    private void clearForm() {
        tfId.clear();
        tfId.setEditable(true); // Re-enable ID for new entry
        tfName.clear();
        tfProgramme.clear();
        cbLevel.setValue(null);
        tfGpa.clear();
        tfEmail.clear();
        tfPhone.clear();
        dpDate.setValue(null);
        cbStatus.setValue(null);
    }
}