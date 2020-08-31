package com.cubeinspire.layout.model;

import javafx.scene.Node;

import java.net.URISyntaxException;

public interface Viewable {
    public void init() throws URISyntaxException;
    public Node getPane();
}
