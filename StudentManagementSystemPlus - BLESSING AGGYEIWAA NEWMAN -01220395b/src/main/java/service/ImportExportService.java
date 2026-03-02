package service;

import domain.Student;
import repository.StudentRepository;
import repository.StudentRepositoryImpl;
import util.AppLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class ImportExportService {
    private final StudentRepository repository;
    private final Random random = new Random();

    public ImportExportService() {
        this.repository = new StudentRepositoryImpl();
    }

    /**
     * Imports a generic CSV file.
     * Expected format: ID,Programme,Email,Phone,Level,Date,Status
     * Returns an array: [successCount, failCount]
     */
    public int[] importFromCSV(String filePath) {
        int successCount = 0;
        int failCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; } // Skip header

                try {
                    String[] tokens = line.split(",");
                    if (tokens.length >= 7) {
                        // 1. Parse Data
                        String id = tokens[0].trim();
                        String programme = tokens[1].trim();
                        String email = tokens[2].trim();
                        String phone = tokens[3].trim();
                        int level = Integer.parseInt(tokens[4].trim());

                        // Handle Date formats (yyyy-MM-dd or yyyy/MM/dd)
                        String dateStr = tokens[5].trim().replace("/", "-");
                        LocalDate date = LocalDate.parse(dateStr.substring(0, 10));

                        String status = tokens[6].trim();

                        // 2. Generate Missing Data (Name and GPA)
                        String fullName = "Student " + id;
                        double gpa = 1.5 + (3.5 * random.nextDouble());
                        gpa = Math.round(gpa * 100.0) / 100.0;

                        // 3. Create Student
                        Student s = new Student(id, fullName, programme, level, gpa, email, phone, date, status);

                        // 4. Save to DB
                        repository.add(s);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    // If ONE row fails (e.g. Duplicate ID), we log it and continue to the next row
                    System.err.println("Skipping row due to error: " + e.getMessage());
                    failCount++;
                }
            }
        } catch (Exception e) {
            AppLogger.logError("File read error: " + e.getMessage());
            e.printStackTrace();
        }
        return new int[]{successCount, failCount};
    }

    /**
     * Used by Main class for automatic initialization.
     */
    public void importInitialData(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }

                String[] tokens = line.split(",");
                if (tokens.length >= 7) {
                    String id = tokens[0].trim();
                    String programme = tokens[1].trim();
                    String email = tokens[2].trim();
                    String phone = tokens[3].trim();
                    int level = Integer.parseInt(tokens[4].trim());
                    String dateStr = tokens[5].trim().replace("/", "-");
                    LocalDate date = LocalDate.parse(dateStr.substring(0, 10));
                    String status = tokens[6].trim();

                    String fullName = "Student " + id;
                    double gpa = 1.5 + (3.5 * random.nextDouble());
                    gpa = Math.round(gpa * 100.0) / 100.0;

                    Student s = new Student(id, fullName, programme, level, gpa, email, phone, date, status);
                    try {
                        repository.add(s);
                    } catch (Exception ignored) {} // Ignore duplicates during startup
                }
            }
            AppLogger.logInfo("Initial data import process completed.");
        } catch (Exception e) {
            AppLogger.logError("Failed to import initial data: " + e.getMessage());
        }
    }

    /**
     * Exports list of students to CSV.
     */
    public void exportToCSV(List<Student> students, String filename) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            StringBuilder sb = new StringBuilder();
            sb.append("ID,Name,Programme,Level,GPA,Email,Phone,Date,Status\n");

            for (Student s : students) {
                sb.append(s.getStudentId()).append(",");
                sb.append(s.getFullName()).append(",");
                sb.append(s.getProgramme()).append(",");
                sb.append(s.getLevel()).append(",");
                sb.append(s.getGpa()).append(",");
                sb.append(s.getEmail()).append(",");
                sb.append(s.getPhone()).append(",");
                sb.append(s.getDate()).append(",");
                sb.append(s.getStatus()).append("\n");
            }
            writer.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}