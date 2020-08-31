package com.cubeinspire.asset.view;

import com.cubeinspire.Controller;
import com.cubeinspire.asset.ControllerAsset;
import com.cubeinspire.asset.model.Asset;
import com.cubeinspire.layout.model.Led;
import com.cubeinspire.layout.model.Ledable;
import com.cubeinspire.layout.model.Viewable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.net.URISyntaxException;

public class View implements Viewable, Ledable {

    private VBox grid;

    public final ChoiceBox<String> tokenSelect;

    private TextField tokenSymbol;
    private TextField tokenDecimals;
    private TextField tokenAddress;
    private Button checkContract;
    private Button uncheckContract;

    VBox erc20group;

    ControllerAsset.Type type;

    Led led;

    public View() {
        this.tokenSelect = new ChoiceBox<String>(FXCollections.observableArrayList(ControllerAsset.Type.ETHER.toString(), ControllerAsset.Type.ERC20.toString()));
        init();
    }

    @Override
    public void init() {


        grid = new VBox();
        grid.setPadding(new Insets(10, 0, 10, 0));

        led = new Led();

        erc20group = new VBox();

        Text scenetitle  = new Text("  Token selection");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

        HBox header = new HBox();
        header.setPadding(new Insets(10, 0, 0, 12));
        header.setSpacing(0);
        header.getChildren().addAll(led.getView(), scenetitle);
        grid.getChildren().addAll(header);

        Text typeTokenLabel = new Text("Type of asset to send");
        VBox typeAsset = new VBox();
        typeAsset.setPadding(new Insets(10, 10, 0, 12));
        typeAsset.setSpacing(5);
        typeAsset.getChildren().addAll(typeTokenLabel, tokenSelect);
        grid.getChildren().addAll(typeAsset);

        Text tokenAddressLabel = new Text("Contract");

        tokenAddress = new TextField("");
        tokenAddress.setMinWidth(150);

        VBox contractWrapper = new VBox();
        contractWrapper.setPadding(new Insets(10, 10, 0, 12));
        contractWrapper.setSpacing(5);
        contractWrapper.getChildren().addAll(tokenAddressLabel, tokenAddress);
        grid.getChildren().addAll(contractWrapper);

        Text tokenDecimalsLabel = new Text("Decimals:");
        tokenDecimals  = new TextField("");
        tokenDecimals.setMaxWidth(60);
        checkContract = new Button("Select");

        HBox decimalsWrapper = new HBox();
        decimalsWrapper.setPadding(new Insets(10, 10, 0, 12));
        decimalsWrapper.setSpacing(5);
        decimalsWrapper.getChildren().addAll(tokenDecimalsLabel, tokenDecimals, checkContract);
        grid.getChildren().addAll(decimalsWrapper);

        Text tokenSymbolLabel = new Text("Symbol:  ");
        tokenSymbol = new TextField("");
        tokenSymbol.setMaxWidth(60);
        uncheckContract = new Button("Change");

        HBox symbolWrapper = new HBox();
        symbolWrapper.setPadding(new Insets(10, 10, 10, 12));
        symbolWrapper.setSpacing(5);
        symbolWrapper.getChildren().addAll(tokenSymbolLabel, tokenSymbol, uncheckContract);
        grid.getChildren().addAll(symbolWrapper);

        erc20group.getChildren().addAll(contractWrapper,decimalsWrapper, symbolWrapper);
        grid.getChildren().addAll(erc20group);

        tokenSelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observableValue, String value, String new_value) {
                switch (new_value) {
                    case "Ether":
                        try {
                            selectEth();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Erc20":
                        try {
                            selectErc20();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Controller.clearAsset();
                }
            }
        });

        uncheckContract.setOnAction(e -> {
            uncheckContract();
        });

        checkContract.setOnAction(e -> {
            tokenAddress.setText(tokenAddress.getText().trim());  // correct issues when pasting with empty spaces.
            try {
                checkContract();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public Node getPane() {
        return grid;
    }

    public void setTokenSymbol(String value) {
        tokenSymbol.setText(value);
    }
    public void setTokenDecimals(String value) {
        tokenDecimals.setText(value);
    }
    public void setTokenAddress(String value) {
        tokenAddress.setText(value);
    }

    public String getTokenDecimals() {
        return tokenDecimals.getText();
    }
    public String getTokenSymbol() {
        return tokenSymbol.getText();
    }
    public String getTokenAddress() {
        return tokenAddress.getText();
    }

    @Override
    public Led.State getState() {  return led.getState(); }

    @Override
    public void setOn() {
        led.setOn();
        disableTokenSymbol();
        disableTokenDecimals();
        checkContract.setDisable(true);
        uncheckContract.setDisable(false);
        tokenAddress.setDisable(true);
    }

    @Override
    public void setOff() {
        led.setOff();
        enableTokenSymbol();
        enableTokenDecimals();
        checkContract.setDisable(false);
        uncheckContract.setDisable(true);
        tokenAddress.setDisable(false);
    }

    public void disableTokenSymbol() {
        tokenSymbol.setDisable(true);
    }

    public void disableTokenDecimals() {
        tokenDecimals.setDisable(true);
    }

    public void enableTokenSymbol() {
        tokenSymbol.setDisable(false);
    }

    public void enableTokenDecimals() {
        tokenDecimals.setDisable(false);
    }

    private void uncheckContract(){
        Controller.clearAsset();
    }

    private void checkContract() throws Exception {
        if(getTokenAddress() != null && getTokenAddress().length() > 40 && Controller.setAssetErc20(getTokenAddress())) { setOn(); }
        else {
            Controller.clearAsset();
            setOff();
        }
    }

    public void selectErc20() throws Exception {
        type = ControllerAsset.Type.ERC20;
        erc20group.setVisible(true);
        checkContract();
    }

    public void selectEth() throws Exception {
        type = ControllerAsset.Type.ETHER;
        setOn();
        erc20group.setVisible(false);
        if(Controller.setAssetEth()) { setOn(); }
        else setOff();
    }

    @Override
    public void setWrong() { led.setWrong(); }
}
