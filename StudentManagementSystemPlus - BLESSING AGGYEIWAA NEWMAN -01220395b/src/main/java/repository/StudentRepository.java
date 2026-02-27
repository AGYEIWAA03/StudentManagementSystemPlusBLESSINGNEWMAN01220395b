package repository;

import domain.Student;
import javafx.collections.ObservableList;
import java.util.List;

public interface StudentRepository {
    void add(Student student) throws Exception;
    void update(Student student) throws Exception;
    void delete(String studentId) throws Exception;
    ObservableList<Student> getAll();
    Student getById(String studentId);
    List<Student> search(String query);
    List<Student> getFiltered(String programme, Integer level, String status); // New
}