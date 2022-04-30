package net.crejak.bcc.model;

import java.time.Duration;

public class Model {
    private Configuration configuration;

    private ClockModel clockModel;

    public Model() {
        this.configuration = new Configuration(Duration.ofMinutes(15), Duration.ZERO);
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
}
