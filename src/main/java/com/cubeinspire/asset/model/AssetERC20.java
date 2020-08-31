package com.cubeinspire.asset.model;

import com.cubeinspire.Controller;
import com.cubeinspire.asset.ControllerAsset;
import com.cubeinspire.wallet.ControllerWallet;
import org.web3j.protocol.admin.Admin;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

public class AssetERC20 implements Asset {

    private String contract;

    private BigInteger decimals;
    private String symbol;
    private boolean isRemoteChecked;
    private boolean isReady;

    private ERC20Token api;

    public AssetERC20(String contract){
        setContract(contract);
    }

    public void setContract(String contract) {
        this.contract = contract;
        api = new ERC20Token(contract);
        isRemoteChecked = false;
        isReady = false;
    }

    @Override
    public boolean remoteCheck(Admin web3jAdmin, Credentials credentials) {
        isRemoteChecked = api.connect(web3jAdmin, credentials);
        symbol = api.getTokenSymbol();
        decimals = BigInteger.valueOf(Long.parseLong(api.getDecimals().toString()));
        return isRemoteChecked;
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public boolean isRemotedChecked() {
        return isRemoteChecked;
    }

    @Override
    public ControllerAsset.Type getType() {
        return ControllerAsset.Type.ERC20;
    }

    @Override
    public BigInteger convertToBasicUnit(double input) {
        BigInteger result = BigInteger.valueOf(0);
        if(decimals != null && decimals.compareTo(BigInteger.valueOf(0)) >= 0) {
            double exponential = Math.pow(10, decimals.intValue());
            double resultDouble = input * exponential;
            result = BigDecimal.valueOf(resultDouble).toBigInteger();
        }
        return result;
    }

    @Override
    public BigDecimal convertToMainUnit(BigInteger balance) {
        return Convert.fromWei(String.valueOf(balance), Convert.Unit.ETHER);
    }

    public String getContract() {
        return contract;
    }

    @Override
    public ERC20Token getToken() { return api; }

    public BigInteger getDecimals() {
        return decimals;
    }

    public String getSymbol() {
        return symbol;
    }

    public ERC20Token getApi() {
        return api;
    }
}
