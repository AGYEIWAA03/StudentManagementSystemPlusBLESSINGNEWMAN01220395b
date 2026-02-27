package service;

import domain.Student;
import javafx.collections.ObservableList;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ImportExportService {

    public int[] importFromCSV(String filePath, StudentService service) {
        int success = 0, failed = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    try {
                        Student s = new Student(
                                data[0].trim(), // ID
                                data[1].trim(), // Name
                                data[2].trim(), // Programme
                                Integer.parseInt(data[3].trim()), // Level
                                Double.parseDouble(data[4].trim()), // GPA
                                data[5].trim(), // Email
                                data[6].trim(), // Phone
                                LocalDate.now(), "Active"
                        );
                        service.addStudent(s);
                        success++;
                    } catch (Exception e) {
                        failed++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[]{success, failed};
    }

    public void exportToCSV(ObservableList<Student> students, String filePath) throws Exception {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("ID,Name,Programme,Level,GPA,Email,Phone,Status");
            for (Student s : students) {
                writer.printf("%s,%s,%s,%d,%.2f,%s,%s,%s%n",
                        s.getStudentId(), s.getFullName(), s.getProgramme(),
                        s.getLevel(), s.getGpa(), s.getEmail(), s.getPhone(), s.getStatus());
            }
        }
    }
}