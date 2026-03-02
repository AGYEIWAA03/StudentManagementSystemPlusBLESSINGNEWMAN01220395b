package repository;

import domain.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class StudentRepositoryImpl implements StudentRepository {

    @Override
    public void add(Student s) throws Exception {
        String sql = "INSERT INTO students(student_id, full_name, programme, level, gpa, email, phone, date, status) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getStudentId());
            pstmt.setString(2, s.getFullName());
            pstmt.setString(3, s.getProgramme());
            pstmt.setInt(4, s.getLevel());
            pstmt.setDouble(5, s.getGpa());
            pstmt.setString(6, s.getEmail());
            pstmt.setString(7, s.getPhone());

            // Store Date safely as String (YYYY-MM-DD)
            if (s.getDate() != null) {
                pstmt.setString(8, s.getDate().toString());
            } else {
                pstmt.setNull(8, Types.VARCHAR);
            }
            pstmt.setString(9, s.getStatus());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Student s) throws Exception {
        String sql = "UPDATE students SET full_name=?, programme=?, level=?, gpa=?, email=?, phone=?, date=?, status=? WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getFullName());
            pstmt.setString(2, s.getProgramme());
            pstmt.setInt(3, s.getLevel());
            pstmt.setDouble(4, s.getGpa());
            pstmt.setString(5, s.getEmail());
            pstmt.setString(6, s.getPhone());

            if (s.getDate() != null) {
                pstmt.setString(7, s.getDate().toString());
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }

            pstmt.setString(8, s.getStatus());
            pstmt.setString(9, s.getStudentId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public ObservableList<Student> getAll() {
        ObservableList<Student> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM students";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // FIX: Read date as String to prevent crash on bad formats
                String dateStr = rs.getString("date");
                LocalDate date = parseDateSafely(dateStr);

                Student s = new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("programme"),
                        rs.getInt("level"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        date,
                        rs.getString("status")
                );
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper to parse dates without crashing
    private LocalDate parseDateSafely(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            // Try standard format YYYY-MM-DD
            return LocalDate.parse(dateStr.substring(0, 10));
        } catch (Exception e) {
            try {
                // Try parsing timestamp (long number)
                long time = Long.parseLong(dateStr);
                return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (Exception ex) {
                System.err.println("Could not parse date: " + dateStr);
                return null; // Return null if totally invalid
            }
        }
    }

    @Override
    public Student getById(String id) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String dateStr = rs.getString("date");
                LocalDate date = parseDateSafely(dateStr);

                return new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("programme"),
                        rs.getInt("level"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        date,
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Student> search(String query) {
        ObservableList<Student> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR full_name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String dateStr = rs.getString("date");
                LocalDate date = parseDateSafely(dateStr);

                list.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("programme"),
                        rs.getInt("level"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        date,
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Student> getFiltered(String programme, Integer level, String status) {
        ObservableList<Student> list = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");

        if (programme != null) sql.append(" AND programme = ?");
        if (level != null) sql.append(" AND level = ?");
        if (status != null) sql.append(" AND status = ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (programme != null) pstmt.setString(index++, programme);
            if (level != null) pstmt.setInt(index++, level);
            if (status != null) pstmt.setString(index++, status);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String dateStr = rs.getString("date");
                LocalDate date = parseDateSafely(dateStr);

                list.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("programme"),
                        rs.getInt("level"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        date,
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}