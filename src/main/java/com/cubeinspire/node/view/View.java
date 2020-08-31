package com.cubeinspire.node.view;

import com.cubeinspire.Controller;
import com.cubeinspire.layout.model.Led;
import com.cubeinspire.layout.model.Ledable;
import com.cubeinspire.layout.model.Viewable;
import com.cubeinspire.node.ControllerNode;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URISyntaxException;

public class View implements Viewable, Ledable {

    private VBox grid;
    TextField nodeAddress;
    TextField nodePort;
    Button buttonConnect;
    Button buttonDisconnect;
    Led led;
    CheckBox saveNode;

    public View() {
        init();
    }

    @Override
    public void init() {

        grid = new VBox();

        led = new Led();

        Text scenetitle = new Text("  Ethereum node");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

        HBox header = new HBox();
        header.setPadding(new Insets(5, 0, 0, 12));
        header.setSpacing(0);

        header.getChildren().addAll(led.getView(), scenetitle);
        grid.getChildren().addAll(header);

        GridPane.setHalignment(scenetitle, HPos.LEFT);

        Text nodeAddressLabel = new Text("Node url");

        String url = ControllerNode.getPrefUrl();
        String port = ControllerNode.getPrefPort();
        saveNode = new CheckBox("Save this node for the next session.");

        if(url != null && url.length() > 0) saveNode.setSelected(true);

        VBox saveWrapper = new VBox();
        saveWrapper.setPadding(new Insets(5, 5, 5, 12));
        saveWrapper.setSpacing(3);
        saveWrapper.getChildren().addAll(saveNode);

        nodeAddress = new TextField(url);
        nodeAddress.setMinWidth(150);

        VBox firstLine = new VBox();
        firstLine.setPadding(new Insets(5, 5, 5, 12));
        firstLine.setSpacing(3);
        firstLine.getChildren().addAll(nodeAddressLabel, nodeAddress);
        grid.getChildren().addAll(firstLine);

        Text nodePortLabel = new Text("Node port");

        nodePort = new TextField(port);

        VBox secondLine = new VBox();
        secondLine.setPadding(new Insets(5, 5, 5, 12));
        secondLine.setSpacing(3);
        secondLine.getChildren().addAll(nodePortLabel, nodePort);

        grid.getChildren().addAll(secondLine,saveWrapper);

        buttonConnect = new Button("Connect");
        buttonDisconnect = new Button("Disconnect");
        buttonDisconnect.setDisable(true);


        buttonConnect.setOnAction(e -> { connect(); });

        buttonDisconnect.setOnAction(e -> { disconnect(); });

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(0, 12, 25, 12));
        hbox.setSpacing(10);
        hbox.getChildren().addAll(buttonConnect, buttonDisconnect);

        grid.getChildren().addAll(hbox);
    }

    @Override
    public Node getPane() {
        return grid;
    }

    public void setAddress(String value) {
        nodeAddress.setText(value);
    }

    public void setPort(String value) {
        nodePort.setText(value);
    }

    public String getAddress() {
        return nodeAddress.getText();
    }
    public String getPort() {
        return nodePort.getText();
    }

    public void connect() {
        buttonConnect.setDisable(true);
        buttonDisconnect.setDisable(false);
        nodeAddress.setDisable(true);
        nodePort.setDisable(true);
        try {
            if( Controller.connectNode(getAddress(), getPort()) ) {
                setOn();
                if(saveNode.isSelected()) ControllerNode.setPrefs(getAddress(), getPort());
            } else {
                setOff();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() { Controller.disconnectNode(); }

    @Override
    public Led.State getState() {  return led.getState(); }

    @Override
    public void setOn() { led.setOn(); }

    @Override
    public void setOff() {
        led.setOff();
        buttonConnect.setDisable(false);
        buttonDisconnect.setDisable(true);
        nodeAddress.setDisable(false);
        nodePort.setDisable(false);
    }

    @Override
    public void setWrong() { led.setWrong(); }
}
