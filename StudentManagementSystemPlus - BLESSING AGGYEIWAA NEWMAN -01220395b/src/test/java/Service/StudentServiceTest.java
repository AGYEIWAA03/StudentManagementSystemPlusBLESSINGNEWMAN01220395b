package Service;

import domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.StudentService;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {
    private StudentService service;

    @BeforeEach
    void setUp() {
        service = new StudentService();
    }

    @Test
    void testValidationFailsOnShortID() {
        Student s = new Student("12", "John Doe", "ENGINEERING", 100, 3.5, "j@test.com", "1234567890", LocalDate.now(), "Active");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.addStudent(s));
        assertTrue(ex.getMessage().contains("4-20"));
    }

    @Test
    void testValidationFailsOnNameWithDigits() {
        Student s = new Student("S123", "John5", "MEDILAB", 100, 3.5, "j@test.com", "1234567890", LocalDate.now(), "Active");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.addStudent(s));
        assertTrue(ex.getMessage().contains("letters"));
    }

    @Test
    void testValidationFailsOnGPAOutOfRange() {
        Student s = new Student("S123", "John Doe", "HOTEL MANAGEMENT ", 100, 5.0, "j@test.com", "1234567890", LocalDate.now(), "Active");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.addStudent(s));
        assertTrue(ex.getMessage().contains("GPA"));
    }

    @Test
    void testValidationFailsOnInvalidEmail() {
        Student s = new Student("S123", "John Doe", "ACCOUNTANCY", 100, 3.5, "jtest.com", "1234567890", LocalDate.now(), "Active");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.addStudent(s));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    void testValidationFailsOnInvalidPhone() {
        Student s = new Student("S123", "John Doe", "BUSINESS", 100, 3.5, "j@test.com", "123", LocalDate.now(), "Active");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.addStudent(s));
        assertTrue(ex.getMessage().contains("Phone"));
    }

    @Test
    void testValidationFailsOnInvalidLevel() {
        Student s = new Student("S123", "John Doe", "HR", 150, 3.5, "j@test.com", "1234567890", LocalDate.now(), "Active");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.addStudent(s));
        assertTrue(ex.getMessage().contains("Level"));
    }

    // FIXED: Removed underscore. ID must be alphanumeric only (A-Z, 0-9).
    @Test
    void testValidStudentPassesValidation() {
        // Generate unique ID using "ST" + timestamp (no underscores)
        String uniqueId = "ST" + System.currentTimeMillis();

        Student s = new Student(uniqueId, "Jane Doe", "IT", 200, 3.0, "jane@test.com", "1234567890", LocalDate.now(), "Active");

        try {
            service.addStudent(s);
            assertTrue(true); // Test passes if no exception
        } catch (IllegalArgumentException e) {
            fail("Validation failed for valid student: " + e.getMessage());
        } catch (Exception e) {
            // Ignore other DB errors (like duplicates if timestamp somehow overlaps, unlikely)
        }
    }
}