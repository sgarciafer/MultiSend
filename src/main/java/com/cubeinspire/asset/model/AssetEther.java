package com.cubeinspire.asset.model;

import com.cubeinspire.asset.ControllerAsset;
import com.cubeinspire.wallet.ControllerWallet;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

public class AssetEther implements Asset {
    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public boolean isRemotedChecked() {
        return true;
    }

    @Override
    public boolean remoteCheck(Admin web3jAdmin, Credentials credentials) {
        return isRemotedChecked();
    }

    @Override
    public ControllerAsset.Type getType() { return ControllerAsset.Type.ETHER; }

    @Override
    public BigInteger convertToBasicUnit(double balance) {
        return Convert.toWei(String.valueOf(balance), Convert.Unit.ETHER).toBigInteger();
    }

    @Override
    public BigDecimal convertToMainUnit(BigInteger balance) {
        return Convert.fromWei(String.valueOf(balance), Convert.Unit.ETHER);
    }

    @Override
    public String getContract() { return null; }

    @Override
    public ERC20Token getToken() { return null; }

    @Override
    public String getSymbol() {
        return "ETH";
    }

    @Override
    public BigInteger getDecimals() {
        return new BigInteger("18");
    }
}
