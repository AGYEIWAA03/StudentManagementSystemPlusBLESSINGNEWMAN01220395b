package util;

import domain.Student;

public class Validator {
    public static void validate(Student s) throws IllegalArgumentException {
        if (s.getStudentId() == null || !s.getStudentId().matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new IllegalArgumentException("Student ID must be 4-20 alphanumeric characters.");
        }
        if (s.getFullName() == null || !s.getFullName().matches("^[a-zA-Z\\s]{2,60}$")) {
            throw new IllegalArgumentException("Name must be 2-60 letters and spaces only.");
        }
        if (s.getEmail() == null || !s.getEmail().matches("^[^@]+@[^@]+\\.[^@]+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (s.getPhone() == null || !s.getPhone().matches("^\\d{10,15}$")) {
            throw new IllegalArgumentException("Phone must be 10-15 digits.");
        }
        if (s.getGpa() < 0.0 || s.getGpa() > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0.");
        }
        int lvl = s.getLevel();
        if (lvl < 100 || lvl > 700 || lvl % 100 != 0) {
            throw new IllegalArgumentException("Invalid Level selected.");
        }
    }
}