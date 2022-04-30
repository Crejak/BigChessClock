package net.crejak.bcc.model;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;

public class ClockModel {
    private final BooleanProperty whiteTurn;
    private final BooleanProperty pause;

    private final LongProperty remainingTimeMillisW;

    private final LongProperty remainingTimeMillisB;

    private Task<Void> clockTask;

    public ClockModel(Configuration configuration) {
        whiteTurn = new SimpleBooleanProperty(true);
        pause = new SimpleBooleanProperty(true);

        long initialTime = configuration.getInitialDuration().getSeconds() * 1000 + configuration.getInitialDuration().getNano() / 1_000_000;
        remainingTimeMillisW = new SimpleLongProperty(initialTime);
        remainingTimeMillisB = new SimpleLongProperty(initialTime);

        clockTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                var lastTime = System.currentTimeMillis();
                while (remainingTimeMillisB.get() > 0 && remainingTimeMillisW.get() > 0) {
                    var currentTime = System.currentTimeMillis();
                    if (!pause.get()) {
                        var elapsedTime = currentTime - lastTime;
                        Platform.runLater(() -> {
                            if (whiteTurn.get()) {
                                remainingTimeMillisW.set(remainingTimeMillisW.get() - elapsedTime);
                            } else {
                                remainingTimeMillisB.set(remainingTimeMillisB.get() - elapsedTime);
                            }
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

    public boolean isWhiteTurn() {
        return whiteTurn.get();
    }

    public BooleanProperty whiteTurnProperty() {
        return whiteTurn;
    }

    public boolean isPause() {
        return pause.get();
    }

    public BooleanProperty pauseProperty() {
        return pause;
    }

    public void start() {
        pause.set(false);
    }

    public LongProperty remainingTimeMillisProperty(Color color) {
        return switch (color) {
            case BLACK -> remainingTimeMillisB;
            case WHITE -> remainingTimeMillisW;
        };
    }
}
