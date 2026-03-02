package service;

import domain.Student;
import repository.StudentRepository;
import repository.StudentRepositoryImpl;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final StudentRepository repository;

    public ReportService() {
        this.repository = new StudentRepositoryImpl();
    }

    public ObservableList<Student> getAllStudents() {
        return repository.getAll();
    }

    public int getTotalStudents() {
        return getAllStudents().size();
    }

    public double getAverageGpa() {
        ObservableList<Student> students = getAllStudents();
        return students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
    }

    public long getActiveCount() {
        return getAllStudents().stream()
                .filter(s -> "Active".equalsIgnoreCase(s.getStatus()))
                .count();
    }

    // NEW: Method for Pie Chart Data (Top Performer, At Risk, Average)
    public Map<String, Number> getPerformanceDistribution() {
        Map<String, Number> map = new LinkedHashMap<>();
        // Initialize keys to ensure order
        map.put("Top Performer", 0);
        map.put("Average", 0);
        map.put("At Risk", 0);

        for (Student s : getAllStudents()) {
            double gpa = s.getGpa();
            if (gpa >= 3.5) {
                incrementValue(map, "Top Performer");
            } else if (gpa < 2.0) {
                incrementValue(map, "At Risk");
            } else {
                incrementValue(map, "Average");
            }
        }
        return map;
    }

    // UPDATED: Method for Bar Chart Data (Specific GPA Classifications)
    public Map<String, Number> getGpaDistribution() {
        Map<String, Number> map = new LinkedHashMap<>();

        // Initialize with requested categories
        map.put("First Class", 0);      // 4.00 - 3.50
        map.put("Second Upper", 0);     // 3.49 - 3.00
        map.put("Second Lower", 0);     // 2.99 - 2.50
        map.put("Third Class", 0);      // 2.49 - 1.80
        map.put("Below Third", 0);      // < 1.80

        for (Student s : getAllStudents()) {
            double gpa = s.getGpa();

            if (gpa >= 3.50) {
                incrementValue(map, "First Class");
            } else if (gpa >= 3.00) {
                incrementValue(map, "Second Upper");
            } else if (gpa >= 2.50) {
                incrementValue(map, "Second Lower");
            } else if (gpa >= 1.80) {
                incrementValue(map, "Third Class");
            } else {
                incrementValue(map, "Below Third");
            }
        }
        return map;
    }

    // Helper method to increment Map values cleanly
    private void incrementValue(Map<String, Number> map, String key) {
        map.merge(key, 1, (oldVal, newVal) -> oldVal.intValue() + newVal.intValue());
    }

    public Map<String, Number> getProgrammeAverageGpa() {
        Map<String, Number> map = new HashMap<>();

        // Group by programme and calculate average
        Map<String, DoubleSummaryStatistics> stats = getAllStudents().stream()
                .collect(Collectors.groupingBy(Student::getProgramme, Collectors.summarizingDouble(Student::getGpa)));

        stats.forEach((programme, summary) -> {
            map.put(programme, summary.getAverage());
        });

        return map;
    }

    public List<Student> getTopPerformers(String programme, int limit) {
        return getAllStudents().stream()
                .filter(s -> programme.equals("All") || s.getProgramme().equalsIgnoreCase(programme))
                .sorted((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Student> getAtRiskStudents(double threshold) {
        return getAllStudents().stream()
                .filter(s -> s.getGpa() < threshold)
                .collect(Collectors.toList());
    }
}