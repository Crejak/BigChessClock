package net.crejak.bcc.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;

import java.util.HashMap;
import java.util.Map;

public class ClockModel {
    private final BooleanProperty pause;

    private final Property<Color> currentColor;
    private final Map<Color, LongProperty> remainingTimeMillis;

    private final Task<Void> clockTask;

    public ClockModel(Configuration configuration) {
        currentColor = new SimpleObjectProperty<>(Color.WHITE);

        pause = new SimpleBooleanProperty(true);

        long initialTime = configuration.getInitialDurationMinutes() * 60L * 1_000L;
        remainingTimeMillis = new HashMap<>();
        remainingTimeMillis.put(Color.WHITE, new SimpleLongProperty(initialTime));
        remainingTimeMillis.put(Color.BLACK, new SimpleLongProperty(initialTime));

        long additionalTime = configuration.getAdditionalDurationSeconds() * 1_000L;
        if (additionalTime > 0) {
            currentColor.addListener((change, oldValue, newValue) -> {
                if (oldValue != newValue) {
                    remainingTimeMillis.get(oldValue).set(remainingTimeMillis.get(oldValue).get() + additionalTime);
                }
            });
        }

        clockTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                var lastTime = System.currentTimeMillis();
                while (getRemainingTimeMillis(Color.WHITE) > 0 && getRemainingTimeMillis(Color.BLACK) > 0) {
                    var currentTime = System.currentTimeMillis();
                    if (!pause.get()) {
                        var elapsedTime = currentTime - lastTime;
                        Platform.runLater(() -> {
                            var remainingTimeProperty = remainingTimeMillis.get(currentColor.getValue());
                            var newTime = Math.max(remainingTimeProperty.get() - elapsedTime, 0);
                            remainingTimeProperty.set(newTime);
                        });
                    }
                    lastTime = currentTime;
                    Thread.sleep(20);
                }
                return null;
            }
        };

        new Thread(clockTask).start();
    }

    public void dispose() {
        clockTask.cancel(true);
    }

    public boolean isPause() {
        return pause.get();
    }

    public BooleanProperty pauseProperty() {
        return pause;
    }

    public Color getCurrentColor() {
        return currentColor.getValue();
    }

    public Property<Color> currentColorProperty() {
        return currentColor;
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor.setValue(currentColor);
    }

    public void start() {
        pause.set(false);
    }

    public LongProperty remainingTimeMillisProperty(Color color) {
        return remainingTimeMillis.get(color);
    }

    public long getRemainingTimeMillis(Color color) {
        return remainingTimeMillis.get(color).get();
    }
}
