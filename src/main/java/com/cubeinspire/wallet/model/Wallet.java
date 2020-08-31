package com.cubeinspire.wallet.model;

import com.cubeinspire.logger.ControllerLogger;
import com.cubeinspire.wallet.ControllerWallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class Wallet {

    // Allows to sign transactions. Not required for low permissions actions.
    private Signer signer;

    private String address;
    private BigInteger nonce;
    private BigInteger etherBalance;  // IN WEI units!
    private Status status;

    private Type type;

    public enum Type{
        KEYSTORE,
        PRIVATEKEY,
        LEDGER
    }

    /**
     * El estatus de la wallet, evita solicitar mas de una
     * vez sus detalles, requiere gestion por una unica funcion
     * para evitar tener los datos mal anotados... si esto resulta
     * ser demasiado complejo a mantener o no ser pertinente por
     * ejemplo en la gestion de un gran numero de wallets tal vez
     * sea mejor no guardar el status y solicitarlo unicamente
     * al efectuar una transaccion o con una accion explicita del
     * usuario.
     */
    public enum Status {
        // La wallet se conectó correctamente y está lista para
        // realizar una transacción. Su nonce y su etherBalance
        // contienen los valores correctos.
        IDLE(0),
        // Una transacción fue enviada y se espera validación.
        // Su nonce y su etherBalance no son correctos.
        TX_PENDING(1),
        // Estado similar a tx pending, salvo que indica que se
        // está intentando anular la transacción enviando eth a sí mismo
        // con gas y limit gas superiores.
        CANCELING(2),
        // Indica que en su último intento, no se pudo conectar y
        // obtener los datos... puede ser por múltiples razones.
        DISCONNECTED(3);

        private int value;
        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public Wallet(ControllerWallet cont, String address) {
        this.address = address;
        status = Status.IDLE;
    }

    /**
     * Pone a dia datos que dependen del estado de la wallet como el nonce o el etherBalance.
     * Este debe ser el único punto que se use para poner al dia la wallet, siendo llamada por
     * otros métodos para este efecto. Activa el status LOCKED si no consigue actualizar la wallet.
     * @return true si se ha puesto al dia los datos correctamente y false si ha habido alguna excepción.
     */
    public boolean refresh(Admin web3jAdmin) {
        boolean isLocked = false;
        try {
            nonce = getNonceRemote(web3jAdmin);
            ControllerLogger.info("Wallet data loaded -- NONCE: "+nonce+" -- ETH: "+ Convert.fromWei(getEthBalance(web3jAdmin).toString(), Convert.Unit.ETHER).toString());
        } catch (InterruptedException e) {
            isLocked = true;
            e.printStackTrace();
            ControllerLogger.error("Error: "+e.getMessage());
        } catch (ExecutionException e) {
            isLocked = true;
            e.printStackTrace();
            ControllerLogger.error("Error: "+e.getMessage());
        } catch (IOException e) {
            isLocked = true;
            e.printStackTrace();
            ControllerLogger.error("Error: "+e.getMessage());
        }
        if(isLocked) status = Status.DISCONNECTED;
        return isLocked;
    }

    /**
     * Verify if the wallet is ready to send. This implies having a not
     * null signer and not having pending transactions or issues on the last
     * refresh of data.
     * @return true if all is ok to send, false if something is wrong.
     */
    public boolean canSend() {
        if (signer != null && this.status == Status.IDLE) return true;
        return false;
    }

    public String getAddress() {
        return address;
    }

    public BigInteger getNonce(Admin web3jAdmin) {
        //if(status == Status.IDLE) return nonce;
        refresh(web3jAdmin);
        return nonce;
    }

    private BigInteger getNonceRemote(Admin web3jAdmin) throws InterruptedException, ExecutionException, IOException {
        ControllerLogger.error("Getting nonce from remote.");
        return web3jAdmin.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
    }

    private BigInteger getEthBalance(Admin web3jAdmin) throws ExecutionException, InterruptedException, IOException {
        ControllerLogger.error("Getting balance from remote.");
        return web3jAdmin.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
    }

    public boolean sendEther(Admin web3jAdmin, String to, BigDecimal ethAmount, BigDecimal gasPrice, BigDecimal gasLimit) throws ExecutionException, InterruptedException, IOException {
        ControllerLogger.info("NONCE:"+getNonceRemote(web3jAdmin));
        ControllerLogger.info("Sending "+ Convert.fromWei(ethAmount.toString(), Convert.Unit.ETHER) +" ether from "+address+" to "+to+". Gwei: "+Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI) +" Gas Limit: "+gasLimit);
        String hexValue = createSignedOfflineTx(to, ethAmount, gasPrice, gasLimit);
        EthSendTransaction ethSendTransaction = web3jAdmin.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        status = Status.TX_PENDING;
        int counter = 0;
        boolean result = false;
        while (true) {
            counter++;
            ControllerLogger.info("NONCE:"+getNonceRemote(web3jAdmin));
            EthGetTransactionReceipt transactionReceipt = web3jAdmin
                    .ethGetTransactionReceipt(transactionHash)
                    .send();
            if (transactionReceipt.getResult() != null) {
                ControllerLogger.info("Tx successful!");
                result = true;
                status = Status.IDLE;
                break;
            }
            if( counter >= 32 ) {
                ControllerLogger.warning("Tx timed out... too low gas? Should you cancel this tx?");
                status = Status.CANCELING;
                break;
            }
            ControllerLogger.info("Tx still not accepted...");
            Thread.sleep(15000);
        }
        return result;
    }

    private String createSignedOfflineTx(String to, BigDecimal ethAmount, BigDecimal gasPrice, BigDecimal gasLimit){
        String hexValue = null;
        RawTransaction rawTx = RawTransaction.createEtherTransaction( nonce,  gasPrice.toBigInteger(), gasLimit.toBigInteger(), to, ethAmount.toBigInteger());
        if (signer != null ){
             hexValue = signer.signMessage(rawTx);
        } else {
            ControllerLogger.error("A problem occurred while signing the transaction.");
        }
        return hexValue;
    }

    public Signer getSigner() {
        return signer;
    }

    public Credentials getCredentials() {
        switch (type) {
            default:
                return null;
            case KEYSTORE:
                return  ((KeyStore)signer).getCredentials();
        }
    }
}
