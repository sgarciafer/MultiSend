package com.cubeinspire.transaction.model;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;

public class PoolItem {

    private final SimpleStringProperty destinationWallet;
    private final SimpleDoubleProperty etherSent;
    private final SimpleDoubleProperty share;
    private final SimpleDoubleProperty toSendAmount;
    private final SimpleStringProperty nonce;
    private final SimpleStringProperty transactionId;
    private final SimpleStringProperty status;
    private RawTransactable tx;
    private TableView<PoolItem> table;

    private double remainderCorrection;

    String statusText;

    public PoolItem(
            TableView table,
            String destinationWallet,
            Double etherSent,
            Double share,
            Double toSendAmount,
            String nonce,
            String transactionId) {
        this.table = table;
        this.destinationWallet = new SimpleStringProperty(destinationWallet);
        this.etherSent = new SimpleDoubleProperty(etherSent);
        this.share = new SimpleDoubleProperty(share);
        this.toSendAmount = new SimpleDoubleProperty(toSendAmount);
        this.nonce = new SimpleStringProperty(nonce);
        this.transactionId = new SimpleStringProperty(transactionId);
        this.status = new SimpleStringProperty("To be sent");
        this.tx = null;
        remainderCorrection = 0.0;
    }

    public void setDestinationWallet(String destinationWallet) {
        this.destinationWallet.set(destinationWallet);
    }

    public void setRemainderCorrection(double val) {
        remainderCorrection = val;
    }

    public double getRemainderCorrection(){
        return remainderCorrection;
    }

    public void setEtherSent(double etherSent) {
        this.etherSent.set(etherSent);
    }

    public void setShare(double share) {
        this.share.set(share);
    }

    public void setToSendAmount(double toSendAmount) {
        this.toSendAmount.set(toSendAmount);
    }

    public void setNonce(String nonce) {
        this.nonce.set(nonce);
    }

    public void setTransactionId() { this.transactionId.set(this.getTx().getTxHash()); }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getDestinationWallet() {
        return destinationWallet.get();
    }

    public SimpleStringProperty destinationWalletProperty() {
        return destinationWallet;
    }

    public double getEtherSent() {
        return etherSent.get();
    }

    public SimpleDoubleProperty etherSentProperty() {
        return etherSent;
    }

    public double getShare() {
        return share.get();
    }

    public SimpleDoubleProperty shareProperty() {
        return share;
    }

    public double getToSendAmount() {
        return toSendAmount.get();
    }

    public SimpleDoubleProperty toSendAmountProperty() {
        return toSendAmount;
    }

    public String getNonce() {
        return nonce.get();
    }

    public SimpleStringProperty nonceProperty() {
        return nonce;
    }

    public String getTransactionId() {
        return transactionId.get();
    }

    public SimpleStringProperty transactionIdProperty() {
        return transactionId;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public RawTransactable getTx() { return tx; }
    public void setTx( RawTransactable txObj ) {
        tx = txObj;
//        tx.addListener(this);
    }
/*
    @Override
    public void onChange(RawTransactable tx) {
        setStatus(tx.getStatus().getValue());
    }*/
}
