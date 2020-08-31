# MultiSend

MultiSend is a standalone desktop application used to perform a large number of transactions on the Ethereum network based a list of receivers and amounts. 
It can be used to send ETH or any ERC20 compatible token.

## 1 Installation

## 1.1 Compiling from source

To use the application you need the last version of Java and JavaFx, both can be installed at the Oracle official download page.

- Java: https://www.oracle.com/be/java/technologies/javase-downloads.html
- JavaFX: https://www.oracle.com/java/technologies/javase/javafx-overview.html

## 1.2 Packaged executable

Verify that the checksum match the one provided and if it does then just click on the file.

You can do so at this website: https://emn178.github.io/online-tools/sha256_checksum.html

DO NOT EXECUTE A FILE FROM AN UNKNOWN SOURCE OR WITHOUT HAVING CHECKED THE CHECKSUM FROM THE OFFICIAL REPO.

## 2 How to use

### 2.1 Ethereum node

Before being able to perform the multiple transactions you need to connect to an Ethereum Node. 
You can create an account at https://infura.io/ and use either the testnet or the mainnet url that will be provided by Infura.
By doing so, you would be submitting the transactions through the Infura Ethereum infrastructure. 
If you want to use the same node url, you can check the checkbox "Save this node for the next session" so you won't have to copy and paste the Infura node again.

### 2.2 Ethereum wallet (KeyStore)

You would also need an Ethereum wallet with a KeyStore file and a password. You can generate such KeyStore Ethereum wallet at MyCrypto.com.
Please, take in consideration that you are rensponsible for securing the password that can be used to read the KeyStore. MultiSend is not saving or storing the password and the credentials are exclusively on RAM memory during the execution of the application and inmeidately destroyed afterwards.
On the current beta version only the KeyStore wallet is available, other options will be developped if the crypto community show interest on this software.

### 3 Token selection

On this area of the interface you can choose the asset that will be submitted. It could be either ETH or any ERC20 token. To use an ERC20 token as asset, select ERC20 form the list the paste the token contract address on the Contract text field then press "Select". The application will search on the Ethereum network the contract and fill the decimals and symbol data for you.

### 4 Receivers data

You can upload a CSV file with "," as separator and an end of line to indicate the end of a row. The first column should be the Ethereum wallet that will receive the transaction and the second column is the ETH amount that has been sent in case of a pool. 
You should then select the total amount of tokens that will be shared between each receiver, then click on "calculate". This will fill the rest of the columns and find the exact amount of tokens that each receiver should get.
Once the receivers data match, you should click on "Lock Receivers".

### 5 Gas price and limit

As on any Ethereum transaction you have to indicate the gas price (in Gwei) and the gas limit. The required gas per transaction depend on the actual congestion of the Ethereum network. You can check the recommended amounts at https://ethgasstation.info/

### 6 Disclaimer

Before being able to use the application you should carefully read and accept the disclaimer 

### 7 Lock and prepare

By clicking on "Lock and Prepare" a last check will be performed, if every requirement to perform the distribution is filled, then a one time small distribution fee will be sent to the developer wallet. The amount is shown in advance just above the "Lock and prepare" button and correspond with 0.002 ETH per receiver, so it may change depending on the number of receivers. 
Please, notice that you can test the application for free on the Ropsten network by setting the Ropsten (Testnet) node. If you do so, a distribution fee will also be sent, but using testnet ethereum that has no value.

### 8 Send next transaction
Once the Lock and prepare phase has been compleated, you will be able to send the transaction one by one by clicking the "Send next transaction" button.
Once a transaction has been sent, the status will be tracked and shown on the receivers table, as well as the transaction id.
The details of the transaction will also be shown on the log area at the bottom of the application.

## **IMPORTANT NOTICE:**

**Please, be aware that even if it has been deeeply tested, the application is still in beta and some bugs could appear on edge cases, that shouldn't affect the transactions themselves, but could interrupt the distribution in some cases. That said, you accept every risk due to a bug that may happen. Also, you accept that the application is not holding any token and is just facilitating a transaction, but the wallet sending the tokens is yours, and therefor you are the only owner of the tokens and responsible for any securities law or any other regulation that could concern such distribution.**
