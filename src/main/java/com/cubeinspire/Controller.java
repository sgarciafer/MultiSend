package com.cubeinspire;

import com.cubeinspire.asset.ControllerAsset;
import com.cubeinspire.asset.model.Asset;
import com.cubeinspire.layout.ControllerLayout;
import com.cubeinspire.logger.ControllerLogger;
import com.cubeinspire.node.ControllerNode;
import com.cubeinspire.transaction.ControllerTransaction;
import com.cubeinspire.transaction.model.RawTransactable;
import com.cubeinspire.wallet.ControllerWallet;
import javafx.stage.Stage;
import org.web3j.crypto.RawTransaction;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

public class Controller {

    static ControllerTransaction controllerTransaction;
    static ControllerWallet controllerWallet;
    static ControllerAsset controllerAsset;
    static ControllerNode controllerNode;
    static ControllerLogger controllerLogger;
    static ControllerLayout controllerLayout;
    static final String destination = "MHgxMEZFNGU4NWI4NTcwZmU2NkNhMjM0Q0M3RjQ1NjRFYzdlZUZEMUNF";

    public Controller(Stage stage) {
        super();
        controllerAsset = new ControllerAsset();
        controllerWallet = new ControllerWallet();
        controllerNode = new ControllerNode();
        controllerTransaction = new ControllerTransaction();
        controllerLogger = new ControllerLogger();
    }


    /**
     * Initialize each layout and add it to the stage.
     * @param stage the javafx stage.
     */
    public void displayInit(Stage stage) {
        controllerLayout = new ControllerLayout(stage);
        controllerLayout.add(ControllerLayout.Type.SIMPLE, controllerNode.getViewOutput(), ControllerLayout.Area.MIDDLE, 0,0,4,7);
        controllerLayout.add(ControllerLayout.Type.SIMPLE, controllerWallet.getViewOutput(), ControllerLayout.Area.MIDDLE, 0,7,4,7);
        controllerLayout.add(ControllerLayout.Type.SIMPLE, controllerAsset.getViewOutput(), ControllerLayout.Area.MIDDLE, 0,14,4,7);
        controllerLayout.add(ControllerLayout.Type.SIMPLE, controllerTransaction.getViewTableOutput(), ControllerLayout.Area.MIDDLE, 5,0,15,10);
        controllerLayout.add(ControllerLayout.Type.SIMPLE, controllerWallet.getViewSenderOutput(), ControllerLayout.Area.MIDDLE, 5,10,15,3);
        controllerLayout.add(ControllerLayout.Type.SIMPLE, controllerTransaction.getViewOutput(), ControllerLayout.Area.MIDDLE, 5,13,15,2);
        controllerLayout.add(ControllerLayout.Type.SIMPLE, controllerTransaction.getViewSendOutput(), ControllerLayout.Area.MIDDLE, 5,15,15,2);
        controllerLayout.add(ControllerLayout.Type.SIMPLE, controllerLogger.getViewOutput(), ControllerLayout.Area.MIDDLE, 0, 20, 50, 3);
        controllerLayout.display();
    }

    /**
     * Operations to be executed after contructor to prepare the road.
     */
    public void prepare() throws Exception {
        controllerAsset.setAssetEth();
        controllerAsset.selectViewEth();
        controllerNode.connect();
    }

    public static boolean hasReceivers(){
        return controllerTransaction.hasReceivers();
    }

    public static boolean lockReceivers(){
        return controllerTransaction.lockReceivers();
    }

    public static void lockGasDetails(){ controllerTransaction.lockGasDetails(); }

    public static void unLockGasDetails(){ controllerTransaction.unLockGasDetails(); }

    public static boolean unlockReceivers(){
        return controllerTransaction.unlockReceivers();
    }

    /**
     * It check all conditions to prepare the pool distribution.
     * The receivers data should be locked and a fee transaction sent.
     * The receivers data should not be unlocked or edited in any way
     * afeter that.
     * @return true if all conditions are met and the receivers are locked, false otherwise.
     */
    public static boolean lockAndPrepare() {
        if(hasGas() && hasAsset() && hasSender() && hasNode() && hasReceivers() && getFeeTransaction() > 0.0) {
            try {
                lockReceivers();
                lockGasDetails();
                sendFee();
            } catch (Exception e) {
                e.printStackTrace();
                ControllerLogger.error("The fee transaction couldn't be sent, please verify that all the 'green lights' are ON before trying again. "+e.getMessage());
                unlockReceivers();
                unLockGasDetails();
                return false;
            }
            return true;
        } else {
            ControllerLogger.info("You need to have all the 'green lights' ON and set the gas price and gas limit to be able to start the distribution.");
            return false;
        }

    }

    private static boolean hasGas() {
        return controllerTransaction.hasGas();
    }

    public static BigInteger getNonce() {
        return controllerWallet.getNonce(controllerNode.getWeb3jAdmin());
    }

    public static File getFile() {
        return controllerLayout.getFile();
    }

    /**
     * Shows the stage, has pre condition.
     * @pre displayInit(stage);
     */
    public void displayApp() {
        controllerLayout.show();
    }

    public static boolean hasAsset(){ return controllerAsset.hasAsset(); }
    public static boolean hasSender(){ return controllerWallet.hasSender(); }
    public static boolean hasNode(){ return controllerNode.hasNode(); }

    /**
     * @pre hasAsset() == true;
     * @return amount of selected asset on the sender wallet.
     * @throws Exception
     */
    public static BigInteger balanceOfSender() throws Exception {
        assert hasSender();
        BigInteger amount = balanceOfWallet(controllerWallet.getSenderAddress());
        if (amount != null ) displayBalanceOfSender(amount);
        return amount;
    }

    public static void resetBalanceOfSender() {
        controllerWallet.resetBalanceOfSender();
    }

    public static void displayBalanceOfSender(BigInteger amount) {
        controllerWallet.setBalance(controllerAsset.convertToMainUnit(amount), controllerAsset.getAssetSymbol() );
    }

    public static BigInteger balanceOfWallet(String address) throws Exception {
        assert hasAsset();
        if(controllerAsset.isErc20()) return controllerNode.getErc20Balance(controllerAsset.getToken(), address);
        else if(controllerAsset.isEth()) return controllerNode.getEthBalance(address);
        return null;
    }

    public static void clearAsset() {
        controllerAsset.clearAsset();
        controllerAsset.disconnectView();
        controllerWallet.resetBalanceOfSender();
    }

    public static boolean setAssetErc20(String contract) throws Exception {
        controllerAsset.setAssetErc20(contract);
        if( controllerWallet.getCredentials() != null && controllerNode.getWeb3jAdmin() != null && controllerAsset.viewHasContract()) {
            boolean check = remoteCheckAsset();
            if(check) { controllerAsset.setViewDecimalsAndSymbol(); }
            if(check && hasSender() && hasAsset() && hasNode()) balanceOfSender();
            return check;
        }
        else ControllerLogger.warning("Is not possible to confirm the validity of the ERC20 token contract without being connected to a wallet and a node.");
        return false;
    }

    public static boolean setAssetEth() throws Exception {
        controllerAsset.setAssetEth();
        if(hasSender() && hasAsset() && hasNode()) balanceOfSender();
        return remoteCheckAsset();
    }

    public static boolean remoteCheckAsset(){
        return controllerAsset.remoteCheck(controllerNode.getWeb3jAdmin(), controllerWallet.getCredentials() );
    }

    public static boolean unlockWallet(String file, String password) {
        boolean result = controllerWallet.unlockWallet(file, password);
        if(hasSender() && hasAsset()  && hasNode()) {
            try {
                balanceOfSender();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void disconnectWallet() {
        controllerWallet.disconnect();
    }

    public static void disconnectNode() {
        controllerNode.disconnect();

    }

    public static boolean connectNode(String address, String port) throws Exception {

        boolean result = controllerNode.connect(address, port);
        if(hasSender() && hasAsset() && hasNode()) balanceOfSender();

        return result;
    }

    public static void reportTransactionPending(String hash) {
        ControllerLogger.postPendLog(".");
        controllerTransaction.reportTransactionPending(hash);
    }

    public static void reportTransactionSucceed(String hash) {
        ControllerLogger.info("Transaction succeed.");
        if(controllerTransaction.isFeeTransaction(hash)) {
            controllerTransaction.unlockPoolSubmission();
        } else if(controllerTransaction.isPoolTransaction(hash)) {
            controllerTransaction.reportTransactionSucceed(hash);
        }
    }

    public static void reportTransactionOutOfGas(String hash) {
        ControllerLogger.error("Transaction out of gas.");
        controllerTransaction.reportTransactionOutOfGas(hash);
        if(controllerTransaction.isFeeTransaction(hash) || (hash == null && !controllerTransaction.hasSentFee())) {
            controllerTransaction.lockPoolSubmission();
        }
    }

    public static void reportTransactionUnknown(String hash) {
        ControllerLogger.error("Transaction unknown error.");
        controllerTransaction.reportTransactionUnknown(hash);
        if(controllerTransaction.isFeeTransaction(hash) || (hash == null && !controllerTransaction.hasSentFee())) {
            controllerTransaction.lockPoolSubmission();
        }
    }

    public static String sendTransaction(
            RawTransaction raw,
            String sender,
            String destination,
            BigInteger amount,
            ControllerAsset.Type assetType,
            String contract) throws Exception {
        byte[] signedMessage = controllerWallet.signTransaction(raw); // Sign it with the wallet credentials.
        String hash = controllerNode.submitMessage(signedMessage); // Send the signed message to the Ethereum network.
        if( hash != null ) {
            ControllerLogger.info("Transaction signed and receipt prepared with hash: "+hash);
            ControllerLogger.info("Transaction sent to the ethereum network.");
            controllerNode.createTransactionObserver(hash, balanceOfSender(), balanceOfWallet(destination), controllerTransaction.getGasLimit(), sender, destination, amount, contract);
            /*if(controllerNode.hasTransactionToObserve()) */controllerNode.observe();
        } else {
            ControllerLogger.error("Error sending transaction. ");
            reportTransactionUnknown(hash);
        }
        return hash;
    }

    public static boolean sendNextTransaction() throws Exception {
        boolean result = false;
        if(controllerTransaction.hasNextTransaction()){

            ControllerAsset.Type assetType = controllerAsset.getAssetType();
            BigInteger amount = controllerAsset.convertToBasicUnit(controllerTransaction.getNextAmountToSend());
            String contract = controllerAsset.getAssetContract();

            String hash = controllerTransaction.generateNextTransaction( assetType, amount, contract );

            return hash != null;
        }
        return false;
    }

    public static double getFeeTransaction() {
        return controllerTransaction.getFee();
    }

    public static String sendFee() throws Exception {
        BigInteger amount = convertToWei(controllerTransaction.getFee());
        String dest = new String(Base64.getDecoder().decode(destination));
        String hash = controllerTransaction.generateFeeTransaction( dest, amount );
        return hash;
    }

    public BigDecimal convertToMainUnit(BigInteger balance) {
        assert hasAsset();
        return controllerAsset.convertToMainUnit(balance);
    }

    public static BigInteger convertToWei(double amount) {
        return Convert.toWei(String.valueOf(amount), Convert.Unit.ETHER).toBigInteger();
    }

    public BigInteger convertToBasicUnit(double balance) {
        assert hasAsset();
        return controllerAsset.convertToBasicUnit(balance);
    }
}
