package com.cubeinspire.asset.model;

import com.cubeinspire.asset.ControllerAsset;
import com.cubeinspire.wallet.ControllerWallet;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.admin.Admin;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface Asset {

    boolean isReady();
    boolean isRemotedChecked();
    boolean remoteCheck(Admin web3jAdmin, Credentials credentials);

    ControllerAsset.Type getType();

    /**
     * It convert the value in a unit to the smallest unit of the asset.
     * @param balance the double amount on the standard unit, for instance ETHER
     * @return the same amount on the smallest unit, for instance WEI
     */
    public BigInteger convertToBasicUnit(double balance);

    public BigDecimal convertToMainUnit(BigInteger balance);

    public String getContract();

    public ERC20Token getToken();

    public String getSymbol();

    public BigInteger getDecimals();
}
