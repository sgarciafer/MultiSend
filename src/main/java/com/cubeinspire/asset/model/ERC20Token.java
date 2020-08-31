package com.cubeinspire.asset.model;

import com.cubeinspire.Controller;
import com.cubeinspire.logger.ControllerLogger;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class ERC20Token {

    private ERC20 contract = null;
    private String tokenAddress;
    private String tokenSymbol;
    private BigInteger decimals;

    public ERC20Token(String address) {
        tokenAddress = address;
    }

    public boolean connect(Admin web3jAdmin, Credentials credentials) {
        boolean result = false;
        try {
            contract = ERC20.load(tokenAddress, web3jAdmin, credentials, new DefaultGasProvider());
            decimals = contract.decimals().sendAsync().get();
            tokenSymbol = contract.symbol().sendAsync().get();
            ControllerLogger.info("ERC20 Token loaded correctly.");
            result = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            ControllerLogger.error("ERC20 couldn't be loaded. "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            ControllerLogger.error("ERC20 couldn't be loaded. "+e.getMessage());
        }
        return result;
    }

    public TransactionReceipt transfer(String to, BigInteger value) {
        TransactionReceipt txReceipt = null;
        try {
            txReceipt = contract.transfer(to, value).sendAsync().get();
            ControllerLogger.info("Transaction error. ");
        } catch (InterruptedException e) {
            e.printStackTrace();
            ControllerLogger.error("Transaction error. "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            ControllerLogger.error("Transaction error. "+e.getMessage());
        }
        return txReceipt;
    }

    public BigInteger balanceOf(String wallet) throws Exception {
        return contract.balanceOf(wallet).send();
    }

    public boolean isConnected() {
        return contract != null;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public ERC20 getContract() {
        return contract;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public BigInteger getDecimals() {
        return decimals;
    }
}
