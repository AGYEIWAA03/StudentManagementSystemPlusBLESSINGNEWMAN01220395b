package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ReportService;
import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {
    private ReportService service;

    @BeforeEach
    void setUp() {
        service = new ReportService();
    }

    @Test
    void testGetTotalStudents() {
        int count = service.getTotalStudents();
        assertTrue(count >= 0);
    }

    @Test
    void testGetAverageGpa() {
        double avg = service.getAverageGpa();
        assertTrue(avg >= 0.0 && avg <= 4.0);
    }

    @Test
    void testGetActiveCount() {
        long count = service.getActiveCount();
        assertTrue(count >= 0);
    }

    @Test
    void testGetGpaDistribution() {
        var map = service.getGpaDistribution();
        assertNotNull(map);
        // Check if keys are valid bands
        assertTrue(map.containsKey("3.5 - 4.0") || map.isEmpty());
    }

    @Test
    void testGetProgrammeAverageGpa() {
        var map = service.getProgrammeAverageGpa();
        assertNotNull(map);
    }

    @Test
    void testTopPerformersLimit() {
        // Test if the limit works
        var list = service.getTopPerformers("All", 5);
        assertTrue(list.size() <= 5);
    }
}