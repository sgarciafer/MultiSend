package com.cubeinspire.layout.model;

public interface Ledable {
    public void setOn();
    public void setOff();
    public void setWrong();
    public Led.State getState();
}
