package net.crejak.bcc.controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.crejak.bcc.controllers.bindings.SafeIntegerStringConverter;
import net.crejak.bcc.model.Color;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController extends AbstractController implements Initializable {
    @FXML
    public ChoiceBox<String> monitorSelectorW;
    @FXML
    public ChoiceBox<String> monitorSelectorB;
    @FXML
    public Button startButton;
    @FXML
    public Label monitorValidationLabel;
    @FXML
    public BorderPane root;
    @FXML
    public TextField initialDurationInput;
    @FXML
    public TextField additionalDurationInput;
    @FXML
    public Label durationValidationLabel;

    private ObservableList<Screen> screenList;

    private ObservableList<String> screenSelectionList;

    private Map<String, Screen> screenMap;

    private Map<Color, Stage> stageMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenList = Screen.getScreens();
        screenSelectionList = FXCollections.observableArrayList();
        screenMap = new HashMap<>();
        stageMap = new HashMap<>();

        screenList.addListener((ListChangeListener<? super Screen>) c -> refreshScreenSelectionList());

        monitorSelectorW.setItems(screenSelectionList);
        monitorSelectorB.setItems(screenSelectionList);

        refreshScreenSelectionList();
        tryPreselectScreens();

        initialDurationInput.textProperty().bindBidirectional(
                model.getConfiguration().initialDurationMinutesProperty().asObject(), new SafeIntegerStringConverter());
        additionalDurationInput.textProperty().bindBidirectional(
                model.getConfiguration().additionalDurationSecondsProperty().asObject(), new SafeIntegerStringConverter());
        initialDurationInput.textProperty().addListener((ChangeListener<? super String>) (c, o, n) -> validate());
        additionalDurationInput.textProperty().addListener((ChangeListener<? super String>) (c, o, n) -> validate());

        validate();
    }

    private boolean validateMonitorSelection() {
        if (Objects.isNull(monitorSelectorW.getValue()) || Objects.isNull(monitorSelectorB.getValue())) {
            monitorValidationLabel.setVisible(true);
            monitorValidationLabel.setText("Please select a display for both players");
            return false;
        } else if (monitorSelectorW.getValue().equals(monitorSelectorB.getValue())) {
            monitorValidationLabel.setVisible(true);
            monitorValidationLabel.setText("The display must be different for both players");
            return false;
        }
        monitorValidationLabel.setVisible(false);
        return true;
    }

    private boolean validateDuration() {
        try {
            Integer.parseInt(initialDurationInput.getText());
            Integer.parseInt(additionalDurationInput.getText());
        } catch (NumberFormatException e) {
            durationValidationLabel.setVisible(true);
            durationValidationLabel.setText("Durations should be integer numbers");
            return false;
        }

        if (model.getConfiguration().getInitialDurationMinutes() <= 0) {
            durationValidationLabel.setVisible(true);
            durationValidationLabel.setText("Initial duration should be positive");
            return false;
        }

        if (model.getConfiguration().getAdditionalDurationSeconds() < 0) {
            durationValidationLabel.setVisible(true);
            durationValidationLabel.setText("Additional duration cannot be negative");
            return false;
        }

        durationValidationLabel.setVisible(false);
        return true;
    }

    private void validate() {
        var isMonitorValid = validateMonitorSelection();
        var isDurationValid = validateDuration();

        var disableButtons = !(isMonitorValid && isDurationValid);

        startButton.setDisable(disableButtons);
    }

    private void refreshScreenSelectionList() {
        screenSelectionList.clear();
        screenMap.clear();
        for (var i = 0; i < screenList.size(); ++i) {
            var screen = screenList.get(i);
            var screenLabel = "Screen " + i + "; " + screen.getBounds().toString() + "; Primary=" + Objects.equals(Screen.getPrimary(), screen);
            screenSelectionList.add(screenLabel);
            screenMap.put(screenLabel, screen);
        }
    }

    private void tryPreselectScreens() {
        if (screenSelectionList.size() >= 2) {
            monitorSelectorW.setValue(screenSelectionList.get(0));
            monitorSelectorB.setValue(screenSelectionList.get(1));
        }
    }

    public void onStartButton(ActionEvent actionEvent) throws IOException {
        model.createClock();

        var boundsW = screenMap.get(monitorSelectorW.getValue()).getBounds();
        var boundsB = screenMap.get(monitorSelectorB.getValue()).getBounds();

        openClockWindow(boundsW, Color.WHITE);
        openClockWindow(boundsB, Color.BLACK);
    }

    private void openClockWindow(Rectangle2D bounds, Color color) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/net/crejak/bcc/views/clock-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setOnKeyPressed(this::onKeyPressed);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stageMap.put(color, stage);
        stage.setScene(scene);
        stage.setTitle(color.name() + " - Clock");
        stage.setY(bounds.getMinY());
        stage.setX(bounds.getMinX());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        ((ClockController) fxmlLoader.getController()).setColor(color);
        stage.show();
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case Q -> {
                stageMap.get(Color.WHITE).close();
                stageMap.get(Color.BLACK).close();

                model.disposeClock();
            }
            case SPACE -> {
                if (model.getClockModel().isPause()) {
                    model.getClockModel().start();
                } else {
                    model.getClockModel().setCurrentColor(switch (model.getClockModel().getCurrentColor()) {
                        case WHITE -> Color.BLACK;
                        case BLACK -> Color.WHITE;
                    });
                }
            }
        }
    }

    public void onMonitorSelection(ActionEvent actionEvent) {
        validate();
    }
}
