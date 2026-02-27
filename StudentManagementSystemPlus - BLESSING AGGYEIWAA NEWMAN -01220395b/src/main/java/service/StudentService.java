package service;

import domain.Student;
import repository.StudentRepository;
import repository.StudentRepositoryImpl;
import util.AppLogger;
import util.Validator;
import javafx.collections.ObservableList;
import java.util.List;

public class StudentService {
    private final StudentRepository repository;

    public StudentService() { this.repository = new StudentRepositoryImpl(); }

    public void addStudent(Student student) throws Exception {
        Validator.validate(student);
        if (repository.getById(student.getStudentId()) != null) {
            throw new IllegalArgumentException("Duplicate Student ID detected.");
        }
        repository.add(student);
        AppLogger.logInfo("Added student: " + student.getStudentId());
    }

    public void updateStudent(Student student) throws Exception {
        Validator.validate(student);
        repository.update(student);
        AppLogger.logInfo("Updated student: " + student.getStudentId());
    }

    public void deleteStudent(String id) throws Exception {
        repository.delete(id);
        AppLogger.logInfo("Deleted student: " + id);
    }

    public ObservableList<Student> getAllStudents() { return repository.getAll(); }

    public List<Student> searchStudents(String query) { return repository.search(query); }

    public List<Student> getFilteredStudents(String programme, Integer level, String status) {
        return repository.getFiltered(programme, level, status);
    }
}