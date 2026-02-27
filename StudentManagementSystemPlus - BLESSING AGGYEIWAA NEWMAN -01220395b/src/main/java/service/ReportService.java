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

    // FIXED: Removed Integer::sum to avoid compilation error with Map<String, Number>
    public Map<String, Number> getGpaDistribution() {
        Map<String, Number> map = new LinkedHashMap<>();

        // Initialize with Integer values
        map.put("0.0 - 1.0", 0);
        map.put("1.0 - 2.0", 0);
        map.put("2.0 - 3.0", 0);
        map.put("3.0 - 4.0", 0);

        for (Student s : getAllStudents()) {
            double gpa = s.getGpa();

            // Use lambda to handle Number addition explicitly
            if (gpa < 1.0) {
                map.merge("0.0 - 1.0", 1, (oldVal, newVal) -> oldVal.intValue() + newVal.intValue());
            } else if (gpa < 2.0) {
                map.merge("1.0 - 2.0", 1, (oldVal, newVal) -> oldVal.intValue() + newVal.intValue());
            } else if (gpa < 3.0) {
                map.merge("2.0 - 3.0", 1, (oldVal, newVal) -> oldVal.intValue() + newVal.intValue());
            } else {
                map.merge("3.0 - 4.0", 1, (oldVal, newVal) -> oldVal.intValue() + newVal.intValue());
            }
        }
        return map;
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