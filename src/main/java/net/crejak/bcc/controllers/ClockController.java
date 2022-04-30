package net.crejak.bcc.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import net.crejak.bcc.model.Color;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class ClockController extends AbstractController implements Initializable {
    @FXML
    public Label timerLabel;
    @FXML
    public BorderPane pane;
    @FXML
    public Label infoLabel;

    private Color color;

    private int sizePixels;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sizePixels = 20;
        pane.widthProperty().addListener((change, oldValue, newValue) -> {
            System.out.println("Change : " + newValue);
            sizePixels = newValue.intValue() / 4;
            updateLabel(model.getClockModel().remainingTimeMillisProperty(color).get());
        });
    }

    public void setColor(Color color) {
        this.color = color;

        model.getClockModel().remainingTimeMillisProperty(color).addListener((change, oldValue, newValue) -> updateLabel((Long) newValue));

        updateLabel(model.getClockModel().remainingTimeMillisProperty(color).get());

        model.getClockModel().pauseProperty().addListener((change, oldValue, newValue) -> updateInformationLabel());
        model.getClockModel().currentColorProperty().addListener((change, oldValue, newValue) -> updateInformationLabel());

        updateInformationLabel();
    }

    private void updateLabel(long value) {
        var duration = Duration.ofMillis(value);
        timerLabel.setText(String.format("%d:%02d.%d", duration.toMinutes(), duration.toSecondsPart(), duration.toMillisPart() / 100));
        updateLabelStyle(value);
    }

    private void updateLabelStyle(long value) {
        StringBuilder sb = new StringBuilder();
        sb.append("-fx-font-size: ")
                .append(sizePixels)
                .append("px;");
        if (value == 0) {
            sb.append(" -fx-text-fill: red;");
        }
        timerLabel.setStyle(sb.toString());
    }

    private void updateInformationLabel() {
        StringBuilder sb = new StringBuilder();

        sb.append("Current screen : ")
                .append(color)
                .append(" | Current player : ")
                .append(model.getClockModel().getCurrentColor().name());

        if (model.getClockModel().isPause()) {
            sb.append(" [Timer is paused, press BACKSPACE to resume]");
        }

        infoLabel.setText(sb.toString());
    }
}
