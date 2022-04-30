package net.crejak.bcc;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.crejak.bcc.model.Model;

public class Application extends javafx.application.Application {
    private Model model;

    @Override
    public void start(Stage stage) throws Exception {
        model = new Model();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Chess clock configuration");
        stage.setScene(scene);
        ((MainController) fxmlLoader.getController()).setModel(model);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
