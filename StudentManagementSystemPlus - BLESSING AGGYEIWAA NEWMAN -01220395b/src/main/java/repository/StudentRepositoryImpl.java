package repository;

import domain.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DatabaseHandler;
import util.AppLogger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentRepositoryImpl implements StudentRepository {

    @Override
    public void add(Student student) throws Exception {
        String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getProgramme());
            pstmt.setInt(4, student.getLevel());
            pstmt.setDouble(5, student.getGpa());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhone());
            pstmt.setString(8, student.getDateAdded().toString());
            pstmt.setString(9, student.getStatus());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Student student) throws Exception {
        String sql = "UPDATE students SET full_name=?, programme=?, level=?, gpa=?, email=?, phone=?, status=? WHERE student_id=?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getProgramme());
            pstmt.setInt(3, student.getLevel());
            pstmt.setDouble(4, student.getGpa());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getStatus());
            pstmt.setString(8, student.getStudentId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String studentId) throws Exception {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public ObservableList<Student> getAll() {
        ObservableList<Student> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            AppLogger.logError("Error fetching students: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Student getById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapResultSetToStudent(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Student> search(String query) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR full_name LIKE ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            pstmt.setString(1, q);
            pstmt.setString(2, q);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToStudent(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Student> getFiltered(String programme, Integer level, String status) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (programme != null) { sql.append(" AND programme = ?"); params.add(programme); }
        if (level != null) { sql.append(" AND level = ?"); params.add(level); }
        if (status != null) { sql.append(" AND status = ?"); params.add(status); }

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToStudent(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"),
                rs.getString("full_name"),
                rs.getString("programme"),
                rs.getInt("level"),
                rs.getDouble("gpa"),
                rs.getString("email"),
                rs.getString("phone"),
                LocalDate.parse(rs.getString("date_added")),
                rs.getString("status")
        );
    }
}