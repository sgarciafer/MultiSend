package com.cubeinspire.asset;

import com.cubeinspire.asset.model.Asset;
import com.cubeinspire.asset.model.AssetERC20;
import com.cubeinspire.asset.model.AssetEther;
import com.cubeinspire.asset.model.ERC20Token;
import com.cubeinspire.asset.view.View;
import javafx.scene.Node;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.admin.Admin;

import java.math.BigDecimal;
import java.math.BigInteger;


public class ControllerAsset {

    private static Asset asset;

    private static View view;

    public enum Type {
        ETHER("Ether"),
        ERC20("Erc20");

        private final String name;

        Type(String name){
            this.name = name;
        }

        @Override
        public String toString(){ return name; }
    }

    public ControllerAsset() {
        view = new View();
    }

    public Node getViewOutput(){ return view.getPane(); }

    public boolean hasAsset() {
        return getAsset() != null;
    }

    public static void clearAsset() {
        setAsset(null);
    }

    public static void disconnectView() {
        view.setOff();
    }

    public boolean isErc20() {
        return getAsset().getType() == Type.ERC20;
    }

    public boolean isEth() {
        return getAsset().getType() == Type.ETHER;
    }

    public boolean remoteCheck(Admin web3jAdmin, Credentials credentials) {
        return asset.remoteCheck(web3jAdmin, credentials);
    }

    public void setAssetErc20(String contract) {
        setAsset(new AssetERC20(contract));
    }

    public boolean viewHasContract() {
        return view.getTokenAddress() != null && view.getTokenAddress().length() > 40;
    }

    public String getAssetSymbol() {
        return getAsset().getSymbol();
    }

    public void setViewDecimalsAndSymbol() {
        String symbol = asset.getSymbol();

        if(symbol != null && symbol.length() > 0 ) {
            view.setTokenSymbol(asset.getSymbol());
//            view.disableTokenSymbol();
        }

        BigInteger decimals = asset.getDecimals();
        if( decimals != null) {
            view.setTokenDecimals(String.valueOf(asset.getDecimals()));
//            view.disableTokenDecimals();
        }
    }

    public void setAssetEth() {
        setAsset(new AssetEther());
    }

    public void selectViewEth() throws Exception {
        view.selectEth();
        view.tokenSelect.getSelectionModel().select(ControllerAsset.Type.ETHER.toString());
    }

    public static BigInteger convertToBasicUnit(double balance){
        return asset.convertToBasicUnit(balance);
    }

    public BigDecimal convertToMainUnit(BigInteger balance){
        return asset.convertToMainUnit(balance);
    }

    public ERC20Token getToken() { return getAsset().getToken(); }

    /*private String getAssetName(){ return asset.getType().toString(); }*/

    public static String getAssetContract() { return getAsset().getContract(); }

    public static Type getAssetType() { return getAsset().getType(); }

    private static Asset getAsset(){ return asset; }

    private static void setAsset(Asset value){ asset = value; }
}
