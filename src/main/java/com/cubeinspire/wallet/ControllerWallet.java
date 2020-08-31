package com.cubeinspire.wallet;

import com.cubeinspire.wallet.view.View;
import com.cubeinspire.logger.ControllerLogger;
import com.cubeinspire.wallet.model.Wallet;
import com.cubeinspire.wallet.view.ViewSender;
import javafx.scene.Node;
import org.web3j.crypto.*;
import org.web3j.protocol.admin.Admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ControllerWallet {

    private static Wallet sender;

    private Credentials credentials;

    View view;
    ViewSender viewSender;

    public ControllerWallet() {
        view = new View();
        viewSender = new ViewSender();
    }

    public Node getViewOutput() {  return view.getPane(); }

    public Node getViewSenderOutput() {  return viewSender.getPane(); }

    public boolean unlockWallet(String file, String password) {
        if(file == null) return false;
        boolean result = true;
        String address;
        try {
            credentials = WalletUtils.loadCredentials(password, file);
            address = credentials.getAddress();
            sender = new Wallet(this, address);
            viewSender.setSender(sender.getAddress());
            ControllerLogger.info("KeyStore wallet unlocked "+sender.getAddress());
            view.setOn();
        } catch (IOException | CipherException e) {
            result = false;
            e.printStackTrace();
            ControllerLogger.error("KeyStore wallet couldn't be unlocked. "+e.getMessage());
            disconnect();
        }
        return result;
    }

    public void disconnect() {
        clearSender();
        viewSender.resetValues();
        clearCredentials();
        view.setOff();
    }

    /**
     * @pre getCredentials() == true;
     * @param raw the transaction to be signed.
     * @return array of bytes with the signed message.
     */
    public byte[] signTransaction(RawTransaction raw) {
        return TransactionEncoder.signMessage(raw, credentials);
    }

    public boolean hasSender() {
        return getSender() != null;
    }

 /*   public boolean hasCredentials() {
        return getCredentials() != null;
    }*/

    public Credentials getCredentials(){
        return credentials;
    }

    private static Wallet getSender(){
        return sender;
    }

    public void clearSender(){
        sender = null;
        viewSender.resetValues();
    }

    public void clearCredentials(){
        credentials = null;
    }

    public static String getSenderAddress() {
        return getSender().getAddress();
    }

    public void resetBalanceOfSender() {
        viewSender.resetBalance();
    }

  /*  public void resetAddressOfSender() {
        viewSender.resetAddress();
    }*/

  /*  public void resetAddressAndBalance() {
        viewSender.resetValues();
    }*/

/*    private void setSender(String sender){
        viewSender.setSender(sender);
    }*/

    public void setBalance(BigDecimal balance, String symbol){
        viewSender.setBalance(balance, symbol);
    }

    public BigInteger getNonce(Admin web3jAdmin) {
        return sender.getNonce(web3jAdmin);
    }
}

