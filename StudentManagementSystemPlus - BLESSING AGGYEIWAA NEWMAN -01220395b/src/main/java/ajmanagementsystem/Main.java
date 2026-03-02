package ajmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.ImportExportService;
import service.StudentService;
import util.DBConnection;
import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 1. COMMENT THIS OUT (Database is already cleared)
        // DBConnection.resetDatabase();

        // 2. Initialize Database
        DBConnection.getConnection();

        // 3. Load Data
        loadInitialDataIfNeeded();

        // 4. Load FXML (Debug Mode)
        // Try loading with leading slash
        URL fxmlLocation = getClass().getResource("/view/Login.fxml");

        // If that failed, try without leading slash (sometimes helps)
        if (fxmlLocation == null) {
            fxmlLocation = getClass().getClassLoader().getResource("view/Login.fxml");
        }

        if (fxmlLocation == null) {
            System.err.println("-------------------------------------------------------");
            System.err.println("FATAL ERROR: Could not find 'Login.fxml'");
            System.err.println("CHECK THESE 3 THINGS:");
            System.err.println("1. File is in: src/main/resources/view/Login.fxml");
            System.err.println("2. File is named EXACTLY 'Login.fxml' (not Login.fxml.txt)");
            System.err.println("3. Right-click 'src/main/resources' -> Mark Directory as -> Resources Root");
            System.err.println("-------------------------------------------------------");
            return;
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("AJ Management System");
        primaryStage.show();
    }

    private void loadInitialDataIfNeeded() {
        StudentService studentService = new StudentService();
        ImportExportService importService = new ImportExportService();

        if (studentService.getAllStudents().isEmpty()) {
            File csvFile = new File("data/students.csv");
            if (csvFile.exists()) {
                System.out.println("Database is empty. Importing data from students.csv...");
                importService.importInitialData("data/students.csv");
            } else {
                System.out.println("data/students.csv not found.");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}