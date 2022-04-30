package net.crejak.bcc.model;

import java.time.Duration;
import java.util.Objects;

public class Model {
    private static Model instance;

    private Configuration configuration;

    private ClockModel clockModel;

    private Model() {
        this.configuration = new Configuration(15, 0);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ClockModel getClockModel() {
        return clockModel;
    }

    public ClockModel createClock() {
        clockModel = new ClockModel(configuration);
        return clockModel;
    }

    public void disposeClock() {
        clockModel.dispose();
        clockModel = null;
    }

    public static Model getInstance() {
        if (Objects.isNull(instance)) {
            instance = new Model();
        }
        return instance;
    }
}
