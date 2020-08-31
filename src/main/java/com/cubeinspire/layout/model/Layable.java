package com.cubeinspire.layout.model;

import com.cubeinspire.Controller;
import com.cubeinspire.layout.ControllerLayout;
import javafx.scene.Node;
import javafx.stage.Stage;

public interface Layable {
    public void display(Stage stage);
    public boolean add(Node node, ControllerLayout.Area area, int col, int row, int large, int tall);
    public ControllerLayout.Type getType();
}
