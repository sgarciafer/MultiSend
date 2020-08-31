package com.cubeinspire.layout;

import com.cubeinspire.layout.model.Layable;
import com.cubeinspire.layout.model.Layout;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ControllerLayout {

    private final Map<Type, Layable> stack;
    private static FileChooser fileChooser;

    private static Stage primaryStage;

    public enum Type{
        SIMPLE,
        ONE_COLUMN_LEFT,
        /*ONE_COLUMN_RIGHT,
        TWO_COLUMNS*/
    }

    public enum Area{
        /*TOP,
        RIGHT,
        BOTTOM,
        LEFT,*/
        MIDDLE
    }

    public ControllerLayout(Stage stage) {
        stack = new HashMap<>();
        primaryStage = stage;
        fileChooser = new FileChooser();

        Layable simple = new Layout("MultiSend [beta]", 1400, 900);
        stack.put(Type.SIMPLE, simple);

        Layable leftCol = new Layout("MultiSend [beta]", 1400, 900);
        stack.put(Type.ONE_COLUMN_LEFT, leftCol);
    }

    public void display() {
        stack.get(Type.SIMPLE).display(primaryStage);
        primaryStage.centerOnScreen();
    }

    public static File getFile() {
        return fileChooser.showOpenDialog(primaryStage);
    }

    public boolean add( Type type, Node node, ControllerLayout.Area area, int col, int row, int large, int tall) {
        return stack.get(type).add(node, area, col, row, large, tall);
    }

    public void show() { primaryStage.show(); }

   /* public Stage getPrimaryStage() {
        return primaryStage;
    }*/
}
