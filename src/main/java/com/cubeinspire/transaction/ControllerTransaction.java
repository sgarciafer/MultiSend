package com.cubeinspire.transaction;

import com.cubeinspire.Controller;
import com.cubeinspire.asset.ControllerAsset;
import com.cubeinspire.layout.model.Led;
import com.cubeinspire.layout.model.ViewController;
import com.cubeinspire.transaction.model.*;
import com.cubeinspire.transaction.view.View;
import com.cubeinspire.transaction.view.ViewSend;
import com.cubeinspire.transaction.view.ViewTable;
import com.cubeinspire.wallet.ControllerWallet;
import javafx.scene.Node;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ControllerTransaction implements ViewController {

    private final View view;

    private static ViewTable viewTable;

    private static ViewSend viewSend;

    // Map the submitted transactions with its tx hash
    private static Map<String, PoolItem> register;

    private static Map<String, RawTransactable> feeRegister;

    public ControllerTransaction() {
        register = new HashMap<>();
        feeRegister = new HashMap<>();
        view = new View();
        viewTable = new ViewTable();
        viewSend = new ViewSend();
    }

    private PoolItem getNext() {
        List<PoolItem> data = viewTable.getTable().getItems();
        for(PoolItem item:data) {
            if( item.getTx() == null && (item.getTransactionId() == null || item.getTransactionId().length() == 0) ) {
                return item;
            }
        }
        return null;
    }

    /**
     * Verify if there is a next todo transaction to be sent.
     * @return true if there is, false if not.
     */
    public boolean hasNextTransaction() {
        return getNext() != null;
    }

    public String generateFeeTransaction(String destination, BigInteger toSendBasicUnit) throws Exception {

        BigInteger nonce = Controller.getNonce(); //.add(new BigInteger("1"));
        RawTransactable newTransactable;
        ControllerAsset.Type assetType = ControllerAsset.Type.ETHER;
        String contract = null;
        String sender = ControllerWallet.getSenderAddress();

        newTransactable = createEthTransactable(nonce, getGasPrice(), getGasLimit());
        if( newTransactable != null ) {
            newTransactable.prepare(destination, toSendBasicUnit);
            newTransactable.create();

            RawTransaction raw = newTransactable.getRawTransaction();

            String hash = Controller.sendTransaction( raw, sender, destination, toSendBasicUnit, assetType, contract);
            feeRegister.put(hash, newTransactable);
            return hash;
        }
        return null;
    }

    /**
     * Generate a raw transaction from the next available PoolItem and add the PoolItem to the register.
     * @param toSendBasicUnit amount to send in wei
     * @return the transaction hash.
     */
    public String generateNextTransaction(
            ControllerAsset.Type type,
            BigInteger toSendBasicUnit,
            String contract ) throws Exception {

        PoolItem next = getNext();

        next.setNonce(String.valueOf(Controller.getNonce()));
        BigInteger nonce = new BigInteger(next.getNonce());
        RawTransactable newTransactable = null;
        if (type == ControllerAsset.Type.ETHER) newTransactable = createEthTransactable(nonce, getGasPrice(), getGasLimit());
        else if (type == ControllerAsset.Type.ERC20 && contract != null) newTransactable = createErc20Transactable(nonce, getGasPrice(), getGasLimit(), contract);

        if( next != null && newTransactable != null ) {
            newTransactable.prepare(next.getDestinationWallet(), toSendBasicUnit);
            newTransactable.create();
            next.setTx(newTransactable);
            next.setNonce(String.valueOf(newTransactable.getNonce()));

            String sender = ControllerWallet.getSenderAddress();
            String dest = newTransactable.getRawTransaction().getTo();
            RawTransaction raw = newTransactable.getRawTransaction();

            ControllerAsset.Type assetType = ControllerAsset.getAssetType();
            BigInteger amount = ControllerAsset.convertToBasicUnit( next.getToSendAmount() );

            String hash = Controller.sendTransaction( raw, sender, dest, amount, assetType, contract);
            next.getTx().setHash(hash);
            next.setTransactionId();
            register.put(hash, next);
            return hash;
        }
        return null;
    }

    public static void reportTransactionPending(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) {
            item.getTx().setStatus(RawTransactable.Status.PENDING);
            item.setStatus(RawTransactable.Status.PENDING.toString());
        }
    }

    public static void reportTransactionSucceed(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) {
            item.getTx().setStatus(RawTransactable.Status.SUCCESS);
            item.setStatus(RawTransactable.Status.SUCCESS.toString());
        }

    }

    public static void reportTransactionOutOfGas(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) {
            item.getTx().setStatus(RawTransactable.Status.OUTOFGAS);
            item.setStatus(RawTransactable.Status.OUTOFGAS.toString());
        }
    }

    public static void reportTransactionUnknown(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) {
            item.getTx().setStatus(RawTransactable.Status.ERROR);
            item.setStatus(RawTransactable.Status.ERROR.toString());
        }
    }

    /*public static void reportTransactionIdle(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) {
            item.getTx().setStatus(RawTransactable.Status.IDLE);
            item.setStatus(RawTransactable.Status.IDLE.toString());
        }
    }*/

    public boolean isFeeTransaction(String hash ){
        if(feeRegister.get(hash) != null) return true;
        return false;
    }
    public boolean isPoolTransaction(String hash ){
        if(register.get(hash) != null) return true;
        return false;
    }

    public boolean hasSentFee() {
        if(feeRegister.size() > 1) {
            Iterator iter = feeRegister.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry pair = (Map.Entry) iter.next();
                RawTransactable value = (RawTransactable)pair.getValue();
                if(value.getStatus() == RawTransactable.Status.SUCCESS) return true;
            }
        }
        return false;
    }

    /**
     * Find the raw transaction of a registered transaction.
     * @param hash of the transaction that is beeing queried.
     * @return the RawTransaction instance.
     */
/*    public RawTransaction getRawFromHash(String hash) {
        RawTransaction result = null;
        PoolItem item = register.get(hash);
        if(item != null) {
            return item.getTx().getRawTransaction();
        }
        return result;
    }*/

    private RawTransactable createEthTransactable(BigInteger nonce, BigInteger GWEI, BigInteger gasLimit ) {
        return new ETHTransaction( nonce, GWEI, gasLimit ); // ETHTransaction
    }

    private RawTransactable createErc20Transactable(BigInteger nonce, BigInteger GWEI, BigInteger gasLimit, String contract ) {
        ERC20Transaction transactable = new ERC20Transaction( nonce, GWEI, gasLimit, contract );
        return transactable;
    }

    public BigInteger getGasPrice() {
        return new BigInteger(view.getGasPrice());
    }

    public BigInteger getGasLimit() {
        return new BigInteger(view.getGasLimit());
    }

  /*  public Double getShareFromHash(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) return item.getShare();
        return null;
    }*/
/*
    public Double getAmountToSendFromHash(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) return item.getToSendAmount();
        return null;
    }

    public Double getNextEthSent(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) return item.getEtherSent();
        return null;
    }

    public String getDestinationFromHash(String hash) {
        PoolItem item = register.get(hash);
        if(item != null ) return item.getDestinationWallet();
        return null;
    }

    public String getStatusFromHash(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) return item.getStatus();
        return null;
    }

    public BigInteger getNonceFromHash(String hash) {
        PoolItem item = register.get(hash);
        if( item != null ) return new BigInteger(item.getNonce());
        return null;
    }*/

    public Double getNextAmountToSend() {
        PoolItem next = getNext();
        if( next != null ) return next.getToSendAmount();
        return null;
    }

    public boolean hasReceivers() {
        return viewTable.getState().equals(Led.State.ON);
    }

    public void lockGasDetails() {  view.lock(); }
    public void unLockGasDetails() { view.unlock(); }

    public boolean lockReceivers(){
        return viewTable.lock();
    }

    public boolean unlockReceivers() {
        return viewTable.unlock();
    }

    @Override
    public Node getViewOutput() {
        return view.getPane();
    }

    public Node getViewTableOutput() {
        return viewTable.getPane();
    }

    public Node getViewSendOutput() {
        return viewSend.getPane();
    }

    public static void setFee(double size) {
        viewSend.setDistributionFee(size);
    }

    public double getFee() {
        return viewSend.getDistributionFee();
    }

   /* public RawTransaction getRawFeeFromHash(String hash) {
        return feeRegister.get(hash).getRawTransaction();
    }*/

/*    public BigInteger getNonceFeeFromHash(String hash) {
        return feeRegister.get(hash).getNonce();
    }*/

    public boolean hasGas() {
        return view.getGasLimit() != null && view.getGasPrice() != null;
    }

    public void unlockPoolSubmission() {
        viewSend.enableSendButton();
    }
    public void lockPoolSubmission() {
        viewSend.disableSendButton();
        viewSend.enableUnlockButton();
    }
}

