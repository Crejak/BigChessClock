package net.crejak.bcc;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/net/crejak/bcc/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 320);
        scene.getStylesheets().add((Objects.requireNonNull(getClass().getResource("/net/crejak/bcc/style/style.css"))).toExternalForm());
        stage.setTitle("Chess clock configuration");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/net/crejak/bcc/images/icon.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
