package com.cubeinspire.transaction.model;

import com.cubeinspire.logger.ControllerLogger;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class PoolCalculator {
    private ObservableList<PoolItem> data;
    private double total;
    private double amountToShare;

    public PoolCalculator(ObservableList<PoolItem> data) {
        this.data = data;
        total = 0;
    }

    public void setTotal() {
        total = 0;
        for(PoolItem item:data){
            total += item.getEtherSent();
        }
        ControllerLogger.info("Total ether sent (table data): "+total);
        if(total > 0 ) {
            for(PoolItem item:data){
                item.setShare((new BigDecimal((item.getEtherSent()/total), MathContext.DECIMAL64)).setScale(8, RoundingMode.HALF_UP).doubleValue());
            }
        } else {
            ControllerLogger.error("The total ether sent should be greater than zero.");
        }

    }

    public double getTotal(){
        return total;
    }

    private void calculateAmountInner() {
        Double remainder = Double.valueOf(amountToShare);
        for(PoolItem item:data) {
            Double itemAmount = item.getShare() * amountToShare;
            remainder -= itemAmount;
            item.setToSendAmount(itemAmount);
        }
        // adjusting value when amounts doesn't match by some decimals.
        if( remainder != 0.0 ) {
            for(PoolItem item:data){
                Double itemRemainder = item.getShare()*remainder;
                item.setRemainderCorrection(itemRemainder);
                item.setToSendAmount( item.getToSendAmount() + itemRemainder );
            }
        }
        ControllerLogger.info("Calculated amount to be sent for each destination wallet.");
    }

    public ObservableList<PoolItem> getData() {
        return data;
    }

    public void calculateAmount(double value){
        amountToShare = value;
        calculateAmountInner();
    }

    /**
     * Check if the data has consistency and can be used to make the transactions.
     * @pre total not null
     * @param total the total amount of tokens/ether to send.
     * @return true if it has consistency, false if not.
     */
    public boolean dataConsistency(double total) {
        boolean result = false;
        double dataTotal = 0.0;
        double shareTotal = 0.0;
        double totalEthSent = 0.0;
        if(total != 0.0) {
            for(PoolItem item: data) {
                dataTotal += item.getToSendAmount();
                shareTotal += item.getShare();
                totalEthSent += item.getEtherSent();
            }
            shareTotal = this.round(shareTotal, 6);
            totalEthSent = this.round(totalEthSent, 6);
            dataTotal = this.round(dataTotal, 6);
            if(shareTotal == 1 && dataTotal == total) {
                result = true;
                ControllerLogger.info("Amount check passed. Total amount to be sent and addition of rows match.");
                ControllerLogger.info("Share check passed. The addition of each share cell is equal to 1.");
            } else {
                if(dataTotal != total) ControllerLogger.error("Amount check failed. The added amount to be sent is "+dataTotal+" DOESN'T match with the entered total amount "+total);
                if(shareTotal != 1) ControllerLogger.error("Share check failed. The addition of each share cell is "+shareTotal+" and should be 1.");
            }
            if(result) {
                for(PoolItem item: data) {
                    if(item.getToSendAmount() > 0.0 || item.getEtherSent() > 0.0 ) {
                        double calcShare = round(item.getEtherSent() / totalEthSent, 3);
                        double tabShare = round(item.getShare(), 3);
                        if( calcShare != tabShare ) {
                            result = false;
                            ControllerLogger.error("Share of wallet "+item.getDestinationWallet()+" is "+tabShare+" and is different to "+calcShare);
                        }
                        double calcToSend = round(total * item.getShare() + item.getRemainderCorrection(), 5);
                        double tabToSend = round(item.getToSendAmount(), 5);
                        if( calcToSend != tabToSend ) {
                            result = false;
                            ControllerLogger.error("Amount to send "+tabToSend+" of wallet "+item.getDestinationWallet()+" is not equal to "+calcToSend);
                        }
                        if(!verifyEthereumAddressFormat(item.getDestinationWallet())) {
                            result = false;
                            ControllerLogger.error("The format ot the wallet "+item.getDestinationWallet()+" is not correct.");
                        }
                    }
                    //if(!result) break;
                }
            }
        }
        return result;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * This is a "soft check", passing it doesn´t give any guarantee that the
     * wallet is an existing Ethereum wallet, but it validate certain common
     * characteristics of ETH wallets, they start by 0x and are followed by
     * 40 hexadecimal characters.
     * @param address the address to verify.
     * @return true if the address comply with the soft check, wrong if it doesn´t
     */
    public static boolean verifyEthereumAddressFormat(String address) {
        boolean result = true;
        
        char[] stringToCharArray = address.toCharArray();
        address = address.replace("0x","");
        char[] hexArray = address.toCharArray();

        if(stringToCharArray.length != 42) result = false;
        if(result) {
            if(stringToCharArray[0] != '0') result = false;
            if(stringToCharArray[1] != 'x') result = false;
            if (!address.matches("^[0-9a-fA-F]+$")) result = false;
        }
        return result;
    }
}
