package com.cubeinspire.transaction.model;

import com.cubeinspire.logger.ControllerLogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
    private ObservableList<PoolItem> data = FXCollections.observableArrayList();

    public CSVReader(TableView table, String csvFile){
        String line = "";
        String cvsSplitBy = ",";
        int row = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] cell = line.split(cvsSplitBy);
                String destWallet;
                Double sentEther;
                Double share;
                Double toSend;
                String nonce;
                String txHash;
                try {
                    if(cell.length <= 0 ) destWallet = "";
                    else destWallet = cell[0];
                    if(cell.length <= 1 ) sentEther = 0.0;
                    else sentEther = Double.parseDouble(cell[1]);
                    if(cell.length <= 2 ) share = 0.0;
                    else share = Double.parseDouble(cell[2]);
                    if(cell.length <= 3 ) toSend = 0.0;
                    else toSend = Double.parseDouble(cell[3]);
                    if(cell.length <= 4 ) nonce = "";
                    else nonce = cell[4];
                    if(cell.length <= 5 ) txHash = "";
                    else txHash = cell[5];
                    data.add(new PoolItem(table, destWallet, sentEther, share, toSend, nonce, txHash));
                } catch (NumberFormatException e) {
                    ControllerLogger.error("The CSV file has wrong format on it on row "+row);
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ObservableList<PoolItem> getData(){
        return data;
    }
}
