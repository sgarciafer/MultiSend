package com.cubeinspire.wallet.model;

import org.web3j.crypto.RawTransaction;

public interface Signer {
    public String signMessage(RawTransaction rawTransaction);
}
