package net.crejak.bcc.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

public class Configuration {

    private IntegerProperty initialDurationMinutes;

    private IntegerProperty additionalDurationSeconds;

    public Configuration(int initialDurationMinutes, int additionalDurationSeconds) {
        this.initialDurationMinutes = new SimpleIntegerProperty(initialDurationMinutes);
        this.additionalDurationSeconds = new SimpleIntegerProperty(additionalDurationSeconds);
    }

    public int getInitialDurationMinutes() {
        return initialDurationMinutes.get();
    }

    public IntegerProperty initialDurationMinutesProperty() {
        return initialDurationMinutes;
    }

    public int getAdditionalDurationSeconds() {
        return additionalDurationSeconds.get();
    }

    public IntegerProperty additionalDurationSecondsProperty() {
        return additionalDurationSeconds;
    }
}
