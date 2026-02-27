package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import util.SettingsManager;

public class SettingsController {
    @FXML private TextField tfRiskThreshold;
    @FXML private Label lblSettingsMsg;

    @FXML
    public void initialize() {
        // Load existing setting (Default 2.0)
        String threshold = SettingsManager.getSetting("riskThreshold", "2.0");
        tfRiskThreshold.setText(threshold);
    }

    @FXML
    private void saveSettings() {
        try {
            double val = Double.parseDouble(tfRiskThreshold.getText());
            if (val < 0 || val > 4.0) {
                throw new NumberFormatException("Out of range.");
            }

            SettingsManager.saveSetting("riskThreshold", String.valueOf(val));
            lblSettingsMsg.setText("Settings saved successfully!");
            lblSettingsMsg.setStyle("-fx-text-fill: green;");
        } catch (NumberFormatException e) {
            lblSettingsMsg.setText("Invalid number. Must be between 0.0 and 4.0");
            lblSettingsMsg.setStyle("-fx-text-fill: red;");
        }
    }
}