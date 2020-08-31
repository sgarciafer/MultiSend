package com.cubeinspire.wallet.model;

import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.io.IOException;

public class KeyStore implements Signer {
    private Credentials credentials;

    public KeyStore(String password, String file) throws IOException, CipherException {
        setCredentials(password, file);
    }

    private void setCredentials(String password, String filePath) throws IOException, CipherException {
        credentials = WalletUtils.loadCredentials(password, filePath);
    }

    @Override
    public String signMessage(RawTransaction rawTransaction) {
        // Get the message signed and encoded.
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        return Numeric.toHexString(signedMessage);
    }

    public Credentials getCredentials() {
        return credentials;
    }

}
