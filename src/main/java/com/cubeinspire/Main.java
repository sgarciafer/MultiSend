package com.cubeinspire;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        controller = new Controller(stage);
        controller.prepare();
        controller.displayInit(stage);
        controller.displayApp();
    }
}
