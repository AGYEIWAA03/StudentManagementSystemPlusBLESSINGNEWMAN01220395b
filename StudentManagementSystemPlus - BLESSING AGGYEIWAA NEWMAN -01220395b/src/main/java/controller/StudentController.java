package controller;

import domain.Student;
import service.StudentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.List;

public class StudentController {
    // Added colEmail to the list
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colId, colName, colProgramme, colStatus, colLevel, colGpa, colEmail;

    @FXML private TextField tfId, tfName, tfProgramme, tfEmail, tfPhone, tfSearch, tfGpa;
    @FXML private ComboBox<String> cbLevel, cbStatus;
    @FXML private ComboBox<String> cbFilterProgramme, cbFilterLevel, cbFilterStatus;
    @FXML private Label lblMessage;

    private StudentService service = new StudentService();
    private Student selectedStudent = null;

    @FXML
    public void initialize() {
        try {
            setupTable();
            setupFilters();
            loadStudents();
        } catch (Exception e) {
            e.printStackTrace();
            if(lblMessage != null) lblMessage.setText("Init Error: " + e.getMessage());
        }
    }

    private void setupTable() {
        if (colId == null) return; // Safety check

        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentId()));
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
        colProgramme.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProgramme()));

        // NEW: Setup Email Column
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));

        colLevel.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getLevel())));
        colGpa.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getGpa())));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));

        studentTable.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            if (newVal != null) { selectedStudent = newVal; populateForm(newVal); }
        });
    }

    private void setupFilters() {
        if (cbLevel != null) cbLevel.getItems().addAll("100", "200", "300", "400", "500", "600", "700");
        if (cbStatus != null) cbStatus.getItems().addAll("Active", "Inactive");

        if (cbFilterProgramme != null) {
            cbFilterProgramme.getItems().addAll("All", "CS", "IT", "General");
            cbFilterProgramme.setValue("All");
        }
        if (cbFilterStatus != null) {
            cbFilterStatus.getItems().addAll("All", "Active", "Inactive");
            cbFilterStatus.setValue("All");
        }
        if (cbFilterLevel != null) {
            cbFilterLevel.getItems().add("All");
            cbFilterLevel.getItems().addAll("100", "200", "300", "400", "500", "600", "700");
            cbFilterLevel.setValue("All");
        }
    }

    private void loadStudents() {
        try {
            studentTable.setItems(service.getAllStudents());
        } catch (Exception e) {
            lblMessage.setText("DB Error: " + e.getMessage());
        }
    }

    private void populateForm(Student s) {
        tfId.setText(s.getStudentId());
        tfId.setEditable(false);
        tfName.setText(s.getFullName());
        tfProgramme.setText(s.getProgramme());
        cbLevel.setValue(String.valueOf(s.getLevel()));
        tfGpa.setText(String.valueOf(s.getGpa()));
        tfEmail.setText(s.getEmail());
        tfPhone.setText(s.getPhone());
        cbStatus.setValue(s.getStatus());
    }

    @FXML private void handleRefresh() { loadStudents(); clearForm(); }

    @FXML
    private void handleAdd() {
        try {
            Student s = getInput();
            service.addStudent(s);
            loadStudents();
            clearForm();
            lblMessage.setText("Added!");
        } catch (Exception e) { lblMessage.setText(e.getMessage()); }
    }

    @FXML
    private void handleUpdate() {
        if (selectedStudent == null) { lblMessage.setText("Select row first"); return; }
        try {
            Student s = getInput();
            service.updateStudent(s);
            loadStudents();
            clearForm();
            lblMessage.setText("Updated!");
        } catch (Exception e) { lblMessage.setText(e.getMessage()); }
    }

    @FXML
    private void handleDelete() {
        Student s = studentTable.getSelectionModel().getSelectedItem();
        if (s == null) return;
        try {
            service.deleteStudent(s.getStudentId());
            loadStudents();
            clearForm();
        } catch (Exception e) { lblMessage.setText(e.getMessage()); }
    }

    @FXML
    private void handleSearch() {
        String q = tfSearch.getText();
        if (q.isEmpty()) loadStudents();
        else studentTable.setItems(FXCollections.observableArrayList(service.searchStudents(q)));
    }

    @FXML
    private void handleFilter() {
        String p = cbFilterProgramme.getValue();
        String l = cbFilterLevel.getValue();
        String s = cbFilterStatus.getValue();
        Integer lvl = (l == null || l.equals("All")) ? null : Integer.parseInt(l);
        List<Student> list = service.getFilteredStudents(p.equals("All") ? null : p, lvl, s.equals("All") ? null : s);
        studentTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void handleSortGPA() {
        studentTable.getItems().sort((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()));
    }

    private Student getInput() throws IllegalArgumentException {
        if (tfId.getText().isEmpty() || tfName.getText().isEmpty()) throw new IllegalArgumentException("ID/Name required");
        return new Student(tfId.getText(), tfName.getText(), tfProgramme.getText(),
                Integer.parseInt(cbLevel.getValue()), Double.parseDouble(tfGpa.getText()),
                tfEmail.getText(), tfPhone.getText(), LocalDate.now(), cbStatus.getValue());
    }

    private void clearForm() {
        selectedStudent = null;
        if(tfId != null) { tfId.clear(); tfId.setEditable(true); }
        if(tfName != null) tfName.clear();
        if(tfProgramme != null) tfProgramme.clear();
        if(tfGpa != null) tfGpa.clear();
        if(tfEmail != null) tfEmail.clear();
        if(tfPhone != null) tfPhone.clear();
        if(cbLevel != null) cbLevel.setValue(null);
        if(cbStatus != null) cbStatus.setValue(null);
    }
}