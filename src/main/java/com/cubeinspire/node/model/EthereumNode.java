package com.cubeinspire.node.model;

public class EthereumNode {
    String address;
    String port;

    public EthereumNode() { }
    public EthereumNode(String address, String port) {
        setAddress(address);
        setPort(port);
    }

    public String getUrl() {
        return getAddress() + ":" + getPort();
    }

    public void setAddress(String address) {
        if (address.endsWith("/")) { address = address.substring(0, address.length() - 1); }
        if(address != null && address.length() > 0) this.address = address.trim();
    }

    public void setPort(String port) {
        if(port != null && port.length() > 0) this.port = port.trim();
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }
}
