package domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Student {
    private String studentId;
    private String fullName;
    private String programme;
    private int level;
    private double gpa;
    private String email;
    private String phone;    // NEW
    private LocalDate date;  // NEW
    private String status;

    // Updated Constructor
    public Student(String studentId, String fullName, String programme, int level, double gpa, String email, String phone, LocalDate date, String status) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.programme = programme;
        this.level = level;
        this.gpa = gpa;
        this.email = email;
        this.phone = phone;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // NEW Getters/Setters for Phone
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // NEW Getters/Setters for Date
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Optional: Helper to get date as String for display if needed elsewhere
    public String getFormattedDate() {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }
        return "";
    }
}