package util;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class UIRefresh {
    private static final List<Runnable> listeners = new ArrayList<>();

    public static void subscribe(Runnable listener) {
        listeners.add(listener);
    }

    // This method ensures the update happens on the main JavaFX thread
    public static void notifyDataChanged() {
        // Platform.runLater ensures this runs safely on the UI thread
        Platform.runLater(() -> {
            for (Runnable listener : listeners) {
                try {
                    listener.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}