package com.cubeinspire.transaction.model;

import com.cubeinspire.Controller;
import com.cubeinspire.logger.ControllerLogger;
import com.cubeinspire.transaction.ControllerTransaction;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ETHTransaction implements RawTransactable {
    private String hash;
    private BigInteger nonce;
    private BigInteger amountToSend;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private BigInteger block;
    private String to;
    private Status status;
//    private List<TransactionListener> listeners;
    private RawTransaction raw;

    public ETHTransaction( BigInteger nonce, BigInteger GWEI, BigInteger gasLimit ) {
        this.nonce = nonce;
        this.gasPrice = Convert.toWei(String.valueOf(GWEI), Convert.Unit.GWEI).toBigInteger();
        this.gasLimit = gasLimit;
//        listeners = new ArrayList<>();
    }

    public void prepare( String toValue, BigInteger amountToSendValue ) {
        to = toValue;
        amountToSend = amountToSendValue;
    }

    public BigInteger getBlock() {
        return block;
    }

    public String getTo() {
        return to;
    }

    public RawTransaction getRaw() {
        return raw;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status value) { status = value; }

    @Override
    public RawTransaction getRawTransaction() {
        return raw;
    }

    @Override
    public RawTransaction create() {
        raw = RawTransaction.createEtherTransaction(nonce,  gasPrice, gasLimit, to, amountToSend);
        return raw;
    }

    /*
    private void notifyListeners() {
        for(TransactionListener item:listeners) {
            item.onChange(this);
        }
    }

    private void observe(ControllerTransaction controller) {
        new Thread(() -> {
            while (true) {
                EthGetTransactionReceipt transactionReceipt = null;
                try {
                    transactionReceipt = controller.getWeb3jAdmin().ethGetTransactionReceipt(getHash()).send();
                } catch (IOException e) {
                    e.printStackTrace();
                    ControllerLogger.error("Transaction receipt exception. "+e.getMessage());
                }
                if (transactionReceipt.getResult() != null) {
                    if(transactionReceipt.getRawResponse() != null) {
                        ControllerLogger.info("Response: "+transactionReceipt.getRawResponse());
                    }
                    BigInteger maxGasHex = new BigInteger(String.valueOf(this.getGasLimit()),10);
                    if( transactionReceipt.getResult().getGasUsed().equals(maxGasHex)) {
                        ControllerLogger.warning("Gas limit reached, high chances that the transaction has not been successful.");
                        if(!controller.balanceChanged()) {
                            ControllerLogger.error("Balance is the same as before sending the transaction. Either there was not enough gas or the contract has locked token transfers.");
                            status = Status.IDLE;
                            controller.removeTransaction();
                            break;
                        }
                    }
                    ControllerLogger.info("Tx successful!");
                    controller.removeTransaction();
                    status = Status.SUCCESS;
                    notifyListeners();
                    break;
                } else {
                    if(transactionReceipt.hasError()) {
                        ControllerLogger.error("Receipt error: "+transactionReceipt.getError());
                    }
                }
                ControllerLogger.postPendLog(".");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ControllerLogger.error("Sleep Obverve interruption exception. "+e.getMessage());
                }
            }
        }).start();
    }
    @Override
    public void addListener(TransactionListener toAdd) {
        listeners.add(toAdd);
    }

    public List<TransactionListener> getListeners() {
        return listeners;
    }
    */

    @Override
    public String getTxHash() {
        return hash;
    }

    @Override
    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public BigInteger getAmountToSend() {
        return amountToSend;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setRaw(RawTransaction raw) {
        this.raw = raw;
    }
}
