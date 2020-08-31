package com.cubeinspire.transaction.view;

import com.cubeinspire.Controller;
import com.cubeinspire.layout.model.Viewable;
import com.cubeinspire.logger.ControllerLogger;
import com.cubeinspire.transaction.model.RawTransactable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ViewSend  implements Viewable {

    VBox grid;

    CheckBox acceptedTerms;

    private double distributionFee;

    private Text distributionFeeText;

    private Button sendNextTransaction;
    private Button lockAndPrepare;

    public ViewSend() {
        init();
    }

    @Override
    public void init() {

        distributionFeeText = new Text("0.0");

        grid = new VBox();

        HBox acceptGroup = new HBox();
        acceptGroup.setPadding(new Insets(5, 5, 5, 12));
        acceptGroup.setSpacing(10);
        Text explanation = new Text("By clicking the 'Lock and prepare' you confirm having verified and accept every detail on the receivers data, gas price and gas limit, \n" +
                "that you are connected to the correct Ethereum node, and that you assume all responsibilities in regard of any securities legislation or other regulation \n" +
                "that would be applicable to the distribution of the tokens. You also accept to pay the Ethereum transaction fee and an extra fee of 0.002 ETH per transaction \n"+
                "for the development and maintenance of the software.\n"+
                "After pressing 'Lock and prepare' you won't be able to edit de receivers data anymore and the distribution fee will be sent. \n"+
                "Also, verify that you have enough ETH to pay for all the transaction fees and the distribution fee before starting the distribution.");


        HBox distFeeGroup = new HBox();
        distFeeGroup.setPadding(new Insets(0, 0, 0, 0));
        distFeeGroup.setSpacing(0);

        Text distributionFeeAsset = new Text(" ETH");
        Text distributionFeeLabel = new Text("Distribution fee:  ");
        distFeeGroup.getChildren().addAll(distributionFeeLabel, distributionFeeText, distributionFeeAsset);

        VBox textAcceptTerms = new VBox();
        textAcceptTerms.setPadding(new Insets(0, 0, 10, 0));
        textAcceptTerms.setSpacing(5);

        textAcceptTerms.getChildren().addAll(explanation, distFeeGroup);

        acceptedTerms = new CheckBox();
        acceptGroup.getChildren().addAll(acceptedTerms, textAcceptTerms);

        lockAndPrepare = new Button("Lock and prepare");
        sendNextTransaction = new Button("Send next transaction");
        lockAndPrepare.setDisable(true);
        sendNextTransaction.setDisable(true);

        acceptedTerms.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                lockAndPrepare.setDisable(!newValue);
            }
        });

        HBox buttonsGroup = new HBox();
        buttonsGroup.setPadding(new Insets(5, 5, 5, 12));
        buttonsGroup.setSpacing(15);
        lockAndPrepare.setOnAction(e -> {
            if(hasAcceptedTerms()) {
                disableUnlockButton();
                if( Controller.lockAndPrepare() )  {
                    acceptedTerms.setDisable(true);
                } else {
                    enableUnlockButton();
                }
            }
        });
        //sendNextTransaction.setDisable(true);
        //TableView.TableViewSelectionModel selectionMode = table.getSelectionModel();
        sendNextTransaction.setOnAction(e -> {
            try {
                if(!Controller.sendNextTransaction()) {
                    ControllerLogger.info("There are no more transactions to be sent");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                ControllerLogger.error(exception.getMessage());
            }
        });
        buttonsGroup.getChildren().addAll(lockAndPrepare, sendNextTransaction);

        grid.getChildren().addAll(acceptGroup, buttonsGroup);
    }

    public boolean hasAcceptedTerms() {
        return acceptedTerms.isSelected();
    }

    public void setDistributionFee(double v) {
        distributionFee = v;
        distributionFeeText.setText(String.valueOf(v));
    }

    public double getDistributionFee() {
        return distributionFee;
    }

    @Override
    public Node getPane() {
        return grid;
    }

    public void enableUnlockButton() {
        lockAndPrepare.setDisable(false);
    }

    public void disableUnlockButton() {
        lockAndPrepare.setDisable(true);
    }

    public void enableSendButton() {
        sendNextTransaction.setDisable(false);
    }

    public void disableSendButton() {
        sendNextTransaction.setDisable(true);
    }
}
