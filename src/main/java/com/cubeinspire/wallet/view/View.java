package com.cubeinspire.wallet.view;

import com.cubeinspire.Controller;
import com.cubeinspire.layout.model.Led;
import com.cubeinspire.layout.model.Ledable;
import com.cubeinspire.layout.model.Viewable;
import com.cubeinspire.logger.ControllerLogger;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URISyntaxException;

public class View implements Viewable, Ledable {

    private VBox grid;
    TextField keyStorePassword;
    Button buttonKeyStore;
    Button buttonUnlockKeyStore;
    Button buttonChangeSenderKeyStore;
    Led led;

    final String[] KeyStorePath = new String[1];

    public View() {
        init();
    }

    @Override
    public void init() {

        grid = new VBox();

        led = new Led();

        Text scenetitle = new Text("  Sender wallet");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

        HBox header = new HBox();
        header.setPadding(new Insets(25, 0, 0, 12));
        header.setSpacing(0);

        header.getChildren().addAll(led.getView(), scenetitle);
        grid.getChildren().addAll(header);

        GridPane.setHalignment(scenetitle, HPos.LEFT);

        Text keyStoreFileLabel = new Text("KeyStore file");

        buttonKeyStore = new Button("Select File");

        buttonKeyStore.setOnAction((e) -> {
            File selectedFile = Controller.getFile();
            if(selectedFile != null) {
                ControllerLogger.info("KeyStore file loaded: "+selectedFile.getName());
                KeyStorePath[0] = selectedFile.getAbsolutePath();
            }
        });

        VBox secondLine = new VBox();
        secondLine.setPadding(new Insets(5, 5, 5, 12));
        secondLine.setSpacing(3);
        secondLine.getChildren().addAll(keyStoreFileLabel, buttonKeyStore);
        grid.getChildren().addAll(secondLine);

        Label keyStorePasswordLabel = new Label("Password");

        keyStorePassword = new PasswordField();
        keyStorePassword.setMaxWidth(100);

        VBox firstLine = new VBox();
        firstLine.setPadding(new Insets(5, 5, 5, 12));
        firstLine.setSpacing(3);
        firstLine.getChildren().addAll(keyStorePasswordLabel, keyStorePassword);
        grid.getChildren().addAll(firstLine);

        buttonChangeSenderKeyStore = new Button("Change sender");
        buttonUnlockKeyStore = new Button("Unlock");
        buttonChangeSenderKeyStore.setDisable(true);


        buttonUnlockKeyStore.setOnAction(e -> {
            Controller.unlockWallet( getKeyStoreFile(), getPassword());
        });

        buttonChangeSenderKeyStore.setOnAction(e -> {
            Controller.disconnectWallet();
        });

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.getChildren().addAll(buttonUnlockKeyStore, buttonChangeSenderKeyStore);
        grid.getChildren().addAll(hbox);
    }

    @Override
    public Node getPane() {
        return grid;
    }

    public void setKeyStoreFile(String value) {
        KeyStorePath[0] = value;
    }

    public String getKeyStoreFile() {
        return KeyStorePath[0];
    }

    public String getPassword() {
        return keyStorePassword.getText();
    }

    @Override
    public Led.State getState() {  return led.getState(); }

    @Override
    public void setOn() {
        led.setOn();
        keyStorePassword.clear();
        buttonKeyStore.setDisable(true);
        buttonUnlockKeyStore.setDisable(true);
        keyStorePassword.setDisable(true);
        buttonChangeSenderKeyStore.setDisable(false);
    }

    @Override
    public void setOff() {
        led.setOff();
        keyStorePassword.clear();
        keyStorePassword.setDisable(false);
        buttonUnlockKeyStore.setDisable(false);
        buttonKeyStore.setDisable(false);
        buttonChangeSenderKeyStore.setDisable(true);
    }

    @Override
    public void setWrong() { led.setWrong(); }
}
