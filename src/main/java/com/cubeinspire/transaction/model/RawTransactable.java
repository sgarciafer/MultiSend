package com.cubeinspire.transaction.model;

import com.cubeinspire.transaction.ControllerTransaction;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;

public interface RawTransactable {

    public enum Status {
        IDLE("To be sent"),      // Idle transaction waiting to be sent.
        SUCCESS("Succeed"),      // Tx was confirmed and less than limit gas was used.
        PENDING("Pending"),      // Tx waiting to be confirmed.
        OUTOFGAS("Out of gas"),  // When the coditions for out of gas are met.
        ERROR("Error"),          // When the conditions for success are not met but it was not possible to determine if it was out of gas or other error.
        CANCELLED("Cancelled");  // When the transaction has been manually cancelled by the user, submitting a second 0 eth tx.
        private String value;
        Status(String value) { this.value = value; }
        @Override public String toString() { return value; }
    }

    public void prepare( String to, BigInteger amountToSend );
    public Status getStatus();
    public void setStatus(Status status);
    public RawTransaction getRawTransaction();
    public RawTransaction create();
//    public void addListener( TransactionListener toAdd );
    public String getTxHash();
    public void setHash(String hash);
    public BigInteger getNonce();
}
