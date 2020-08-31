package com.cubeinspire.wallet.view;

import com.cubeinspire.layout.model.Viewable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.math.BigDecimal;

public class ViewSender implements Viewable {

    VBox wrapper;
    Text address;
    Text balance;
    Text symbol;

    String initMessage = "Unlock a wallet to send the assets from";

    public ViewSender() {
        init();
    }

    @Override
    public void init() {
        wrapper = new VBox();
        Text addressLabel = new Text("Sender:");
        addressLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        Text balanceLabel = new Text("Balance:");
        balanceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        address = new Text(initMessage);
        address.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        balance = new Text("");
        balance.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        symbol = new Text("");
        symbol.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        HBox addressLine = new HBox();

        addressLine.setPadding(new Insets(10, 10, 0, 25));
        addressLine.setSpacing(10);
        addressLine.getChildren().addAll(addressLabel, address);

        HBox balanceLine = new HBox();

        balanceLine.setPadding(new Insets(10, 10, 10, 25));
        balanceLine.setSpacing(10);
        balanceLine.getChildren().addAll(balanceLabel, balance, symbol);


        VBox innerWrapper = new VBox();

        innerWrapper.setPadding(new Insets(10, 10, 10, 10));
        innerWrapper.setSpacing(10);
        innerWrapper.getChildren().addAll(addressLine, balanceLine);
        innerWrapper.setStyle("" +
                "-fx-background-color: #EEE; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-radius: 10px; " +
                "-fx-border-color:#D8D8D8;" +
                "-fx-border-width: 1px;");
        innerWrapper.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, Color.color(0.85,0.85,0.85),2,0,0,0));


        wrapper.setPadding(new Insets(0, 10, 0, 25));
        wrapper.setSpacing(10);
        wrapper.getChildren().addAll(innerWrapper);
    }

    @Override
    public Node getPane() {
        return wrapper;
    }

    public void resetBalance() {
        setBalance(new BigDecimal("0.0"), "");
    }

    public void resetAddress() {
        setSender(initMessage);
    }

    public void resetValues() {
        resetAddress();
        resetBalance();
    }

    public void setSender(String sender) {
        address.setText(sender);
    }

    public void setBalance(BigDecimal amount, String token) {
        symbol.setText(token);
        balance.setText(String.valueOf(amount));
    }
}
