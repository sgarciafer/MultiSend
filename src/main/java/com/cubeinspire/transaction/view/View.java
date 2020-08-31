package com.cubeinspire.transaction.view;

import com.cubeinspire.layout.model.Viewable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class View implements Viewable {

    GridPane grid;

    TextField gasPrice;
    TextField gasLimit;

    public View() {
        grid = new GridPane();
        init();
    }

    @Override
    public void init() {

        HBox wrapper = new HBox();
        wrapper.setPadding(new Insets(15, 5, 15, 12));
        wrapper.setSpacing(15);


        HBox priceWrapper = new HBox();
        priceWrapper.setPadding(new Insets(5, 5, 5, 12));
        priceWrapper.setSpacing(15);
        Text gasPriceLabel = new Text("Tx Price(Gwei):");

        gasPrice = new TextField();
        gasPrice.setDisable(false);
        gasPrice.setMaxWidth(80);

        priceWrapper.getChildren().addAll(gasPriceLabel, gasPrice);

        HBox limitWrapper = new HBox();
        limitWrapper.setPadding(new Insets(5, 5, 5, 12));
        limitWrapper.setSpacing(15);
        Text gasLimitLabel = new Text("Gas limit:");

        gasLimit = new TextField();
        gasLimit.setDisable(false);
        gasLimit.setMaxWidth(80);

        Pattern pattern = Pattern.compile("\\d*");
        TextFormatter formatter2 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        gasLimit.setTextFormatter(formatter);
        gasPrice.setTextFormatter(formatter2);
        //gasPrice.setTextFormatter(formatter2);

        limitWrapper.getChildren().addAll(gasLimitLabel, gasLimit);

        wrapper.getChildren().addAll(priceWrapper, limitWrapper);
        grid.add(wrapper, 0, 0);
    }

    @Override
    public Node getPane() {
        return grid;
    }

    public String getGasPrice() { return gasPrice.getText(); }

    public String getGasLimit() {
        return gasLimit.getText();
    }

    public void lock() {
        gasPrice.setDisable(true);
        gasLimit.setDisable(true);
    }

    public void unlock() {
        gasPrice.setDisable(false);
        gasLimit.setDisable(false);
    }
}
