package net.crejak.bcc.model;

import java.time.Duration;

public class Configuration {
    private Duration initialDuration;
    private Duration additionalDuration;

    public Configuration(Duration initialDuration, Duration additionalDuration) {
        this.initialDuration = initialDuration;
        this.additionalDuration = additionalDuration;
    }

    public Duration getInitialDuration() {
        return initialDuration;
    }

    public void setInitialDuration(Duration initialDuration) {
        this.initialDuration = initialDuration;
    }

    public Duration getAdditionalDuration() {
        return additionalDuration;
    }

    public void setAdditionalDuration(Duration additionalDuration) {
        this.additionalDuration = additionalDuration;
    }
}
