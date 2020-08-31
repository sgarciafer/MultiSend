package com.cubeinspire.node;

import com.cubeinspire.Controller;
import com.cubeinspire.asset.model.ERC20Token;
import com.cubeinspire.layout.model.Led;
import com.cubeinspire.layout.model.ViewController;
import com.cubeinspire.logger.ControllerLogger;
import com.cubeinspire.node.model.TransactionObserver;
import com.cubeinspire.node.view.View;
import javafx.scene.Node;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

public class ControllerNode implements ViewController {

    private static Admin web3jAdmin;

    private final List<TransactionObserver> register;

    private static final Preferences prefs = Preferences.userRoot().node("surge/cerberus");

    private static View view;

    public ControllerNode() {
        view = new View();
        register = new ArrayList<>();
    }

    public Node getViewOutput() {
        return view.getPane();
    }

    public boolean connect() {
        if(connect(view.getAddress(), view.getPort())) {
            view.connect();
            return true;
        }
        return false;
    }

    public static String getPrefUrl() {
        return prefs.get("url", "");
    }

    public static String getPrefPort() {
        return prefs.get("port", "");
    }

    public static void setPrefs(String url, String port) {
        prefs.put("url", url);
        prefs.put("port", port);
    }

    public static boolean connect(String url, String port) {
        if( port.trim().length() > 0) {  ControllerLogger.info("Node: "+url+" port:"+port); }
        else { ControllerLogger.info("Node: "+url); }
        web3jAdmin = Admin.build(new HttpService(url));
        return testNodeConnection();
    }

    private static boolean testNodeConnection() {
        try {
            Web3ClientVersion resp = web3jAdmin.web3ClientVersion().send();
            ControllerLogger.info("Connected to Ethereum client version: " + resp.getWeb3ClientVersion());
            view.setOn();
        } catch (IOException | ClientConnectionException e) {
            // e.printStackTrace();
            ControllerLogger.error("Problem connecting to the Ethereum node. "+e.getMessage());
            view.setOff();
            return false;
        } catch(IllegalArgumentException e) {
            ControllerLogger.error("You cannot leave the Address field empty. "+e.getMessage());
            view.setOff();
            return false;
        }

        return true;
    }

    /*public String getAddress() { return view.getAddress(); }*/
    public String getPort() { return view.getPort(); }
    public Led.State getState() {  return view.getState(); }

    public BigInteger getErc20Balance(ERC20Token api, String wallet) throws Exception {
        return api.balanceOf(wallet);
    }

    public boolean hasNode() {
        return web3jAdmin != null;
    }

    public BigInteger getEthBalance(String wallet) throws ExecutionException, InterruptedException {
        EthGetBalance ethGetBalance = getWeb3jAdmin().ethGetBalance(wallet, DefaultBlockParameterName.LATEST).sendAsync().get();
        return ethGetBalance.getBalance(); // wei
    }

   /* public boolean hasTransactionToObserve() {
        if(register.size() > 0 ) {
            for(TransactionObserver item: register) {
                if(!item.getCompleted()) {
                    return true;
                }
            }
        }
        return false;
    }*/

    private TransactionObserver getNextObserver(){
        for(TransactionObserver item:register) {
            if(!item.getCompleted()) return item;
        }
        return null;
    }

    public void observe() {
        TransactionObserver item = getNextObserver();
        new Thread(() -> {
            while (true) {
                EthGetTransactionReceipt receipt = null;
                try { receipt = item.getTransactionReceipt(); } catch (IOException e) { e.printStackTrace();}
                if(receipt.getResult() != null && !item.limitGasReached()) {
                    // Transaction successful.
                    Controller.reportTransactionSucceed(item.getHash());
                    item.setCompleted();
                    break;
                } else if (receipt.getResult() != null && item.limitGasReached()) {
                    // Should verify if the transaction has succeed or not...
                    BigInteger currentBalance = null;
                    BigInteger currentBalanceDest = null;
                    try { currentBalance = Controller.balanceOfSender(); } catch (Exception e) { e.printStackTrace(); }
                    try { currentBalanceDest = Controller.balanceOfWallet(item.getDestinationWallet()); } catch (Exception e) { e.printStackTrace(); }
                    item.setBalanceAfter(currentBalance);
                    item.setDestinationBalanceAfter(currentBalanceDest);
                    if(!item.balanceMatch()) { // Out of gas... limit reached but balance of destination has not changed.
                        Controller.reportTransactionOutOfGas(item.getHash());
                        item.setCompleted();
                        break;
                    } else { // Seem like an extremely weird case where gas limit was exactly the gas used but it was not out of gas.
                        Controller.reportTransactionUnknown(item.getHash());
                        item.setCompleted();
                        break;
                    }
                } else {
                    Controller.reportTransactionPending(item.getHash());
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException ex)
                    {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }

    public void createTransactionObserver(String hash, BigInteger balance, BigInteger balanceDest, BigInteger gasLimit,String sender, String dest, BigInteger amount, String contract) {
        register.add(new TransactionObserver(web3jAdmin, hash, balance, balanceDest, gasLimit, sender, dest, amount, contract));
    }

    public Admin getWeb3jAdmin() { return web3jAdmin; }

    /**
     * Submits a signed message to the Ethereum network and report back.
     * @param signedMessage the signed message.
     * @return the hash of the transaction
     */
    public String submitMessage(byte[] signedMessage) throws Exception {
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = getWeb3jAdmin().ethSendRawTransaction(hexValue).sendAsync().get();
        if(ethSendTransaction.hasError()) { ControllerLogger.error(ethSendTransaction.getError().getMessage()); }  // Error sending the message, output the error.
        return ethSendTransaction.getTransactionHash();
    }

    public void disconnect(){
        web3jAdmin.shutdown();
        web3jAdmin = null;
        view.setOff();
    }
}
