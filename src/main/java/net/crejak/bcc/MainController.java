package net.crejak.bcc;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import net.crejak.bcc.model.Color;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
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
    public Label informationLabel;
    @FXML
    public Button startButton;
    @FXML
    public Button testButton;
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

    private Stage stageW;
    private Stage stageB;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenList = Screen.getScreens();
        screenSelectionList = FXCollections.observableArrayList();
        screenMap = new HashMap<>();

        screenList.addListener((ListChangeListener<? super Screen>) c -> {
            refreshScreenSelectionList();
        });

        monitorSelectorW.setItems(screenSelectionList);
        monitorSelectorB.setItems(screenSelectionList);

        monitorValidationLabel.managedProperty().bind(monitorValidationLabel.visibleProperty());
        durationValidationLabel.managedProperty().bind(durationValidationLabel.visibleProperty());

        refreshScreenSelectionList();

        validateMonitorSelection();

        initialDurationInput.textProperty().addListener((ChangeListener<? super String>) (c, o, n) -> {
            validate();
        });
        additionalDurationInput.textProperty().addListener((ChangeListener<? super String>) (c, o, n) -> {
            validate();
        });
    }

    @Override
    protected void onModelSet() {
        initialDurationInput.setText(Long.toString(model.getConfiguration().getInitialDuration().toMinutes()));
        initialDurationInput.setText(Long.toString(model.getConfiguration().getAdditionalDuration().toSeconds()));
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
            int initialDuration = Integer.parseInt(initialDurationInput.getText());
            int additionalDuration = Integer.parseInt(additionalDurationInput.getText());
            model.getConfiguration().setInitialDuration(Duration.ofMinutes(initialDuration));
            model.getConfiguration().setAdditionalDuration(Duration.ofSeconds(additionalDuration));
        } catch (NumberFormatException e) {
            durationValidationLabel.setVisible(true);
            durationValidationLabel.setText("Durations should be integer numbers");
            return false;
        }

        if (model.getConfiguration().getInitialDuration().isZero() || model.getConfiguration().getInitialDuration().isNegative()) {
            durationValidationLabel.setVisible(true);
            durationValidationLabel.setText("Initial duration should be positive");
            return false;
        }

        if (model.getConfiguration().getAdditionalDuration().isNegative()) {
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

        testButton.setDisable(disableButtons);
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

    public void onTestButton(ActionEvent actionEvent) throws IOException {
        model.createClock();

        var boundsW = screenMap.get(monitorSelectorW.getValue()).getBounds();
        var boundsB = screenMap.get(monitorSelectorB.getValue()).getBounds();

        FXMLLoader fxmlLoaderW = new FXMLLoader(getClass().getResource("clock-view.fxml"));
        Scene sceneW = new Scene(fxmlLoaderW.load());
        sceneW.setOnKeyPressed(this::onKeyPressed);
        stageW = new Stage(StageStyle.UNDECORATED);
        stageW.setScene(sceneW);
        stageW.setTitle("White - clock");
        stageW.setX(boundsW.getMinX());
        stageW.setY(boundsW.getMinY());
        stageW.setWidth(boundsW.getWidth());
        stageW.setHeight(boundsW.getHeight());
        ((ClockController) fxmlLoaderW.getController()).setColor(Color.WHITE);
        ((ClockController) fxmlLoaderW.getController()).setModel(model);
        stageW.show();

        FXMLLoader fxmlLoaderB = new FXMLLoader(getClass().getResource("clock-view.fxml"));
        Scene sceneB = new Scene(fxmlLoaderB.load());
        sceneB.setOnKeyPressed(this::onKeyPressed);
        stageB = new Stage(StageStyle.UNDECORATED);
        stageB.setScene(sceneB);
        stageB.setTitle("Black - clock");
        stageB.setY(boundsB.getMinY());
        stageB.setX(boundsB.getMinX());
        stageB.setWidth(boundsB.getWidth());
        stageB.setHeight(boundsB.getHeight());
        ((ClockController) fxmlLoaderB.getController()).setColor(Color.BLACK);
        ((ClockController) fxmlLoaderB.getController()).setModel(model);
        stageB.show();

        ((Stage) root.getScene().getWindow()).hide();
    }

    public void onStartButton(ActionEvent actionEvent) throws IOException {

    }

    public void onKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case Q -> {
                stageW.close();
                stageB.close();

                model.disposeClock();

                ((Stage) root.getScene().getWindow()).show();
            }
            case BACK_SPACE -> {
                model.getClockModel().start();
            }
            case SPACE -> {
                model.getClockModel().whiteTurnProperty().set(!model.getClockModel().isWhiteTurn());
            }
        }
    }

    public void onMonitorSelection(ActionEvent actionEvent) {
        validate();
    }
}
