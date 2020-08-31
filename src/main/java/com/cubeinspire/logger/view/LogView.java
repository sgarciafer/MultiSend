package com.cubeinspire.logger.view;

import com.cubeinspire.layout.model.Viewable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LogView extends TextArea implements Viewable {

    VBox wrapper;

    public LogView() {
        super();
        init();
    }

    @Override
    public void init() {
        wrapper = new VBox();
        wrapper.setPadding(new Insets(30, 0, 0, -14));
        this.setEditable(false);
        this.setFont(Font.font("Unicode", 13));
        wrapper.getChildren().addAll(this);
        wrapper.setMinHeight(190);
        wrapper.setMaxHeight(190);
    }

    @Override
    public Node getPane() {
        return wrapper;
    }

    public void postPendLog(String logChars) {
        this.setText(this.getText()+logChars);
        goToBottom();
    }

    public void addLog(String logLine) {
        this.setText(this.getText()+"\n"+logLine);  // add a new line
        goToBottom();
    }

    private void goToBottom() {
        this.selectPositionCaret(this.getLength());  // hackish way to scroll to the bottom
        this.deselect();  // we don't want to keep the text selected
    }
}
