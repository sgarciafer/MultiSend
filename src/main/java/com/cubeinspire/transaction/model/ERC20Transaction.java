package com.cubeinspire.transaction.model;

import com.cubeinspire.transaction.model.ETHTransaction;
import com.cubeinspire.transaction.model.RawTransactable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

public class ERC20Transaction extends ETHTransaction implements RawTransactable {

    private String contract;

    public ERC20Transaction(BigInteger nonce, BigInteger GWEI, BigInteger gasLimit, String contract ) {
        super(nonce, GWEI, gasLimit );
        this.contract = contract;
    }
    

    @Override
    public RawTransaction create() {
        String encodedFunction = FunctionEncoder.encode(transfer());
        this.setRaw(RawTransaction.createTransaction( this.getNonce(), this.getGasPrice(), this.getGasLimit(), contract, encodedFunction));
        return getRaw();
    }

    /**
     * Encode an ABI function, required by createTransaction to generate the tx data.
     * @return the ABI function as Function class instance.
     */
    private Function transfer() {
        return new Function(
                "transfer",
                Arrays.asList(new Address(this.getTo()), new Uint256(this.getAmountToSend())),
                Collections.singletonList( new TypeReference<Bool>() { } )
        );
    }
}
