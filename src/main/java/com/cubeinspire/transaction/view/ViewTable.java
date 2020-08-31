package com.cubeinspire.transaction.view;

import com.cubeinspire.Controller;
import com.cubeinspire.layout.model.Led;
import com.cubeinspire.layout.model.Ledable;
import com.cubeinspire.layout.model.Viewable;
import com.cubeinspire.logger.ControllerLogger;
import com.cubeinspire.transaction.ControllerTransaction;
import com.cubeinspire.transaction.model.CSVReader;
import com.cubeinspire.transaction.model.PoolCalculator;
import com.cubeinspire.transaction.model.PoolItem;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ViewTable implements Viewable, Ledable {

    private TableView<PoolItem> table = new TableView<>();
    private final ObservableList<PoolItem> data = FXCollections.observableArrayList();

    private GridPane grid;

    private Led led;

    private Button buttonCsvFile;
    private Button calculateShare;
    private Button buttonLockData;
    private Button buttonUnlockData;
    private TextField amountToShareField;

    public ViewTable() {
        init();
    }

    @Override
    public void init() {
        table.setEditable(true);

        grid = new GridPane();

        led = new Led();

        tableAddData(table, data);

        table.setMaxHeight(180);
        table.setMinHeight(180);
        table.setMaxWidth(1000);
        table.setMinWidth(1000);

        TableView.TableViewSelectionModel selectionMode = table.getSelectionModel();

        Button addNewLine = new Button("Add row");
        addNewLine.setMinWidth(100);
        addNewLine.setOnAction(e -> {
            ArrayList<PoolItem> actual =  new ArrayList<PoolItem>(table.getItems());
            actual.add(new PoolItem( table,"", 0.0, 0.0, 0.0, "0", ""));
            table.getItems().clear();
            table.setItems(toObservableList(actual));
            table.setSelectionModel(selectionMode);
            refresh();
        });
       // grid.add(addNewLine, 0, 4, 1, 1);


        Text title = new Text("   Receivers data");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

        HBox titleGroup = new HBox();
        titleGroup.setPadding(new Insets(5, 5, 5, 0));
        titleGroup.setSpacing(0);
        titleGroup.getChildren().addAll(led.getView(), title);

        Text csvFileLabel = new Text("Update table:");
        //grid.add(csvFileLabel, 1, 2);

        buttonCsvFile = new Button("CSV file");
        buttonCsvFile.setMaxWidth(100);

        final String[] csvPath = new String[1];
        buttonCsvFile.setOnAction((e) -> {
            File selectedFile = Controller.getFile();
            if(selectedFile != null) {
                csvPath[0] = selectedFile.getAbsolutePath();
                CSVReader reader = new CSVReader( table, csvPath[0]);
                ObservableList<PoolItem> data = reader.getData();
                Double amount = Double.valueOf("0"+"."+"0"+"0"+"2");
                ControllerTransaction.setFee(data.size() * amount);
                table.setItems(data);
                refresh();
            }
        });

        HBox csvGroup = new HBox();
        csvGroup.setPadding(new Insets(5, 5, 5, 0));
        csvGroup.setSpacing(15);
        csvGroup.getChildren().addAll(csvFileLabel, buttonCsvFile);
        //grid.add(buttonCsvFile, 2, 2);

        Text amountToShare = new Text("Amount to share:");

        amountToShareField = new TextField("");
        calculateShare = new Button("Calculate");

        HBox calculos = new HBox();
        calculos.setPadding(new Insets(5, 5, 5, 0));
        calculos.setSpacing(15);

        //grid.add(buttonLockData, 3, 4, 2, 1);
        //grid.add(buttonUnlockData, 4, 4, 2, 1);

        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        amountToShareField.setTextFormatter(formatter);
        amountToShareField.setMaxWidth(100);

        //grid.add(amountToShare, 3, 7);
        //grid.add(amountToShareField, 3, 7);

        boolean[] tableLock = new boolean[1];
        tableLock[0] = false;
        table.setOnSort((event) -> {
            if(tableLock[0]) event.consume();
        });

        calculateShare.setOnAction(e -> {
            ControllerLogger.info("calculate share");
            PoolCalculator calc = new PoolCalculator(table.getItems());
            calc.setTotal();
            String amount = "0.0";
            if(amountToShareField.getText() != null) amount =  amountToShareField.getText();
            calc.calculateAmount(Double.parseDouble(amount));
            refresh();
        });

        buttonLockData = new Button("Lock receivers");
        buttonUnlockData = new Button("Unlock");
        buttonUnlockData.setDisable(true);
        //TableView.TableViewSelectionModel selectionMode = table.getSelectionModel();

        buttonLockData.setOnAction(e -> {
            TableView.TableViewSelectionModel sel = table.getSelectionModel();
            sel.clearSelection();
            PoolCalculator calc = new PoolCalculator(table.getItems());
            if(amountToShareField.getText().length() > 0 && calc.dataConsistency(Double.parseDouble(amountToShareField.getText()))){
                setOn();
                ControllerLogger.info("The distribution data has passed the required checks.");
            } else {
                setWrong();
                ControllerLogger.error("There are errors on the distribution data.");
            }
            buttonUnlockData.setDisable(false);
            buttonLockData.setDisable(true);
            tableLock[0] = true;
            table.setEditable(false);
            table.setSelectionModel(null);
            amountToShareField.setDisable(true);
            calculateShare.setDisable(true);
            addNewLine.setDisable(true);
            buttonCsvFile.setDisable(true);
        });

        buttonUnlockData.setOnAction(e -> {
            setOff();
            buttonLockData.setDisable(false);
            buttonUnlockData.setDisable(true);
            tableLock[0] = false;
            table.setEditable(true);
            ControllerLogger.info("selection mode:"+selectionMode);
            table.setSelectionModel(selectionMode);
            amountToShareField.setDisable(false);
            calculateShare.setDisable(false);
            addNewLine.setDisable(false);
            buttonCsvFile.setDisable(false);
        });

        calculos.getChildren().addAll(csvFileLabel, buttonCsvFile, amountToShare, amountToShareField, calculateShare, buttonLockData, buttonUnlockData);

        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(5, 5, 5, 25));
        wrapper.setSpacing(15);
        wrapper.getChildren().addAll(titleGroup, calculos, table);
        grid.add(wrapper, 0, 2, 15, 15);
    }

    public void refresh() {
        table.refresh();
    }

    public void tableAddData(TableView table, ObservableList<PoolItem> data) {
        //Create a customer cell factory so that cells can support editing.
        Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                return new EditingCell();
            }
        };

        //Create a customer cell factory Double so that cells can support editing.
        Callback<TableColumn, TableCell> cellFactoryDouble = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                return new EditingCellDouble();
            }
        };

        //Create a customer cell factory Double so that cells can support editing.
        Callback<TableColumn, TableCell> cellFactoryInteger = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                return new EditingCellInteger();
            }
        };

        TableColumn destinationWallet = new TableColumn("Destination wallet");
        destinationWallet.setCellValueFactory(
                new PropertyValueFactory<PoolItem, String>("destinationWallet"));
        destinationWallet.setCellFactory(cellFactory);

        TableColumn etherSent = new TableColumn("Ether sent");
        etherSent.setCellValueFactory(
                new PropertyValueFactory<PoolItem, Double>("etherSent"));
        etherSent.setCellFactory(cellFactoryDouble);

        TableColumn share = new TableColumn("Share");
        share.setCellValueFactory(
                new PropertyValueFactory<PoolItem, Double>("share"));
        share.setCellFactory(cellFactoryDouble);

        TableColumn toSendAmount = new TableColumn("To send amount");
        toSendAmount.setCellValueFactory(
                new PropertyValueFactory<PoolItem, Double>("toSendAmount"));
        toSendAmount.setCellFactory(cellFactoryDouble);

        TableColumn nonce = new TableColumn("Nonce");
        nonce.setCellValueFactory(
                new PropertyValueFactory<PoolItem, String>("nonce"));
        nonce.setCellFactory(cellFactory);

        TableColumn transactionId = new TableColumn("Transaction Id");
        transactionId.setCellValueFactory(
                new PropertyValueFactory<PoolItem, String>("transactionId"));
        transactionId.setCellFactory(cellFactory);

        TableColumn status = new TableColumn("Status");
        status.setCellValueFactory(
                new PropertyValueFactory<PoolItem, String>("status"));
        status.setCellFactory(cellFactory);

        transactionId.sortableProperty().setValue(false);
        destinationWallet.sortableProperty().setValue(false);
        status.sortableProperty().setValue(false);

        etherSent.prefWidthProperty().bind(table.widthProperty().multiply(0.06));
        share.prefWidthProperty().bind(table.widthProperty().multiply(0.06));
        toSendAmount.prefWidthProperty().bind(table.widthProperty().multiply(0.155));
        destinationWallet.prefWidthProperty().bind(table.widthProperty().multiply(0.245));
        nonce.prefWidthProperty().bind(table.widthProperty().multiply(0.035));
        transactionId.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
        status.prefWidthProperty().bind(table.widthProperty().multiply(0.07));

        table.setItems(data);

        table.getColumns().addAll(destinationWallet, etherSent, share, toSendAmount, nonce, transactionId, status);

        //Make the table editable
        table.setEditable(true);

        //Modifying the firstName property
        destinationWallet.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PoolItem, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PoolItem, String> t) {
                ((PoolItem) t.getTableView().getItems().get(t.getTablePosition().getRow())).setDestinationWallet(t.getNewValue());
            }
        });

        //Modifying the firstName property
        etherSent.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PoolItem, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PoolItem, Double> t) {
                ((PoolItem) t.getTableView().getItems().get(t.getTablePosition().getRow())).setEtherSent(t.getNewValue());
            }
        });

        //Modifying the firstName property
        share.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PoolItem, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PoolItem, Double> t) {
                ((PoolItem) t.getTableView().getItems().get(t.getTablePosition().getRow())).setShare(t.getNewValue());
            }
        });

        //Modifying the firstName property
        toSendAmount.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PoolItem, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PoolItem, Double> t) {
                ((PoolItem) t.getTableView().getItems().get(t.getTablePosition().getRow())).setToSendAmount(t.getNewValue());
            }
        });

        //Modifying the firstName property
        nonce.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PoolItem, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PoolItem, Integer> t) {
                ((PoolItem) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNonce(String.valueOf(t.getNewValue()));
            }
        });

        //Modifying the firstName property
        transactionId.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PoolItem, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PoolItem, String> t) {
                ((PoolItem) t.getTableView().getItems().get(t.getTablePosition().getRow())).setTransactionId(); //t.getNewValue()
            }
        });

        //Modifying the firstName property
        status.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PoolItem, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PoolItem, String> t) {
                ((PoolItem) t.getTableView().getItems().get(t.getTablePosition().getRow())).setStatus(t.getNewValue());
            }
        });

    }

    private ObservableList<PoolItem> toObservableList(ArrayList<PoolItem> data) {
        // extractor to observe change of person properties
        Callback<PoolItem, Observable[]> extractor = (PoolItem p) -> {
            return new Observable[]{
                    p.destinationWalletProperty(),
                    p.etherSentProperty(),
                    p.shareProperty(),
                    p.toSendAmountProperty(),
                    p.nonceProperty(),
                    p.transactionIdProperty(),
                    p.statusProperty()
            };
        };
        // make list observable and attach extractor
        ObservableList<PoolItem> observablePoolItems = FXCollections.observableList(data, extractor);
        return observablePoolItems;
    }

    @Override
    public Node getPane() {
        return grid;
    }

    @Override
    public void setOn() {
        led.setOn();
    }

    @Override
    public void setOff() {
        led.setOff();
    }

    @Override
    public void setWrong() {
        led.setWrong();
    }

    @Override
    public Led.State getState() {
        return led.getState();
    }

    public Integer size() {
        ObservableList items = table.getItems();
        int size = items.size();
        return table.getItems().size();
    }


    public boolean lock() {
        buttonCsvFile.setDisable(true);
        calculateShare.setDisable(true);
        buttonLockData.setDisable(true);
        buttonUnlockData.setDisable(true);
        amountToShareField.setDisable(true);
        table.setEditable(false);
        return true;
    }

    public boolean unlock() {
        buttonCsvFile.setDisable(false);
        calculateShare.setDisable(false);
        buttonLockData.setDisable(false);
        buttonUnlockData.setDisable(false);
        amountToShareField.setDisable(false);
        table.setEditable(true);
        return true;
    }

    public TableView<PoolItem> getTable() {
        refresh();
        return table;
    }

}
