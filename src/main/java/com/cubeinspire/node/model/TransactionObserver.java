package com.cubeinspire.node.model;

import com.cubeinspire.logger.ControllerLogger;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;

public class TransactionObserver {
    BigInteger balanceBefore;
    BigInteger balanceAfter;
    BigInteger destinationBalanceBefore;
    BigInteger destinationBalanceAfter;
    BigInteger gasLimit;
    BigInteger gasUsed;
    String hash;
    String senderWallet;
    String destinationWallet;
    BigInteger amountSent;
    String contractAddress;
    Response.Error lastError;

    Boolean completed;

    private Admin web3jAdmin;

    public TransactionObserver(
            Admin web3jAdminVal,
            String hashVal,
            BigInteger balance,
            BigInteger balanceDest,
            BigInteger gasLimitValue,
            String sender,
            String dest,
            BigInteger amount,
            String contract) {
        balanceBefore = balance;
        destinationBalanceBefore = balanceDest;
        hash = hashVal;
        web3jAdmin = web3jAdminVal;
        gasLimit = gasLimitValue;
        gasUsed = new BigInteger("0");
        senderWallet = sender;
        destinationWallet = dest;
        amountSent = amount;
        contractAddress = contract;
        completed = false;
    }

    public void setBalanceAfter( BigInteger balance ) {
        balanceAfter = balance;
    }
    public void setDestinationBalanceAfter( BigInteger balance ) {
        balanceAfter = balance;
    }

    public boolean balanceMatch() {
        assert balanceBefore != null && balanceAfter != null && destinationBalanceBefore != null && destinationBalanceAfter != null;
        if ( contractAddress != null && balanceBefore.equals(balanceAfter.add(amountSent)) && destinationBalanceAfter.equals(destinationBalanceAfter.add(amountSent))) return true;
        if ( contractAddress == null && destinationBalanceAfter.equals(destinationBalanceAfter.add(amountSent))) return true; // sender balance cannot match due to gas expenses.
        return false;
    }

    public EthGetTransactionReceipt getTransactionReceipt() throws IOException {
        EthGetTransactionReceipt transactionReceipt = null;
        transactionReceipt = web3jAdmin.ethGetTransactionReceipt(hash).send();
        if (transactionReceipt.getResult() != null) {
            gasUsed = transactionReceipt.getResult().getGasUsed();
        } else {
            if(transactionReceipt.hasError()) {
                lastError = transactionReceipt.getError();
            }
        }
        return transactionReceipt;
    }

    public boolean limitGasReached() {
        return gasUsed.equals(gasLimit);
    }

    public String getDestinationWallet() {
        return destinationWallet;
    }

    public String getHash() {
        return hash;
    }

    public boolean hasError() {
        return lastError != null;
    }

    public Response.Error getError() {
        return lastError;
    }

    public Boolean getCompleted() { return completed; }

    public void setCompleted() { this.completed = true; }

}
