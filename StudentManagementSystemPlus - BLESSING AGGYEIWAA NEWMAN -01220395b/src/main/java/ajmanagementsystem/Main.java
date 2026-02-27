package ajmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // The path MUST match the folder structure: src/main/resources/fxml/Login.fxml
            String fxmlPath = "/fxml/Login.fxml";
            URL fxmlLocation = Main.class.getResource(fxmlPath);

            if (fxmlLocation == null) {
                // Specific error if file is missing
                System.err.println("------------------------------------------------");
                System.err.println("CRITICAL ERROR: File not found -> " + fxmlPath);
                System.err.println("1. Check if 'Login.fxml' is inside 'src/main/resources/fxml/'");
                System.err.println("2. Check if the folder name 'fxml' is lowercase.");
                System.err.println("------------------------------------------------");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            Scene scene = new Scene(fxmlLoader.load(), 900, 500);
            stage.setTitle("AJ Management System - Login");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}