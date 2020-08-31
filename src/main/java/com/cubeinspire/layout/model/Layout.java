package com.cubeinspire.layout.model;

import com.cubeinspire.layout.ControllerLayout;
import com.cubeinspire.layout.model.Layable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Layout implements Layable {

    private Integer screenWidth;
    private Integer screenHeight;
    private ControllerLayout.Type type;
    private String title;


    private final VBox vbox = new VBox();
    private GridPane grid = addGridPane();
    private BorderPane border;

    public Layout(String title, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        this.title = title;
        border = new BorderPane();
        type = ControllerLayout.Type.SIMPLE;
    }

    @Override
    public void display(Stage stage) {
        stage.setTitle(title);
        stage.getIcons().add(new Image("logoimage.png"));
        Scene scene = new Scene(border, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    @Override
    public boolean add(Node node, ControllerLayout.Area area, int col, int row, int large, int tall) {
        if(area == ControllerLayout.Area.MIDDLE) {
            System.out.println(node.toString());
            grid.add(node, col, row, large, tall);
            border.setCenter(grid);
            return true;
        }
        return false;
    }

    @Override
    public ControllerLayout.Type getType() {
        return type;
    }

    /*
     * Creates a grid for the center region with four columns and three rows
     */
    private GridPane addGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 40, 10));
        return grid;
    }
}
