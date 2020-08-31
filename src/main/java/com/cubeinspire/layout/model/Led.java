package com.cubeinspire.layout.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Led {
    final static Image imageLedOff = new Image("loff.png");
    final static Image imageLedOn = new Image("lon.png");
    final static Image imageLedWrong = new Image("lwrong.png");

    private State state;

    ImageView led;

    public enum State {
        ON,
        OFF,
        WRONG
    }

    public Led() {
        led = new ImageView();
        led.setImage(imageLedOff);
        state = State.OFF;
        led.setFitHeight(15);
        led.setFitWidth(15);
    }

    public State getState() {
        return state;
    }

    public ImageView getView() {
        return led;
    }

    public void setOn() {
        led.setImage(imageLedOn);
        state = State.ON;
    }

    public void setOff() {
        led.setImage(imageLedOff);
        state = State.OFF;
    }

    public void setWrong() {
        led.setImage(imageLedWrong);
        state = State.WRONG;
    }

}
