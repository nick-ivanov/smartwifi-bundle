package com.thinklab.smartwifi;
import android.os.Environment;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Wallet {
    public static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
    public static final BigInteger GAS_LIMIT_DEFAULT = BigInteger.valueOf(4_700_000L);
    // Create new wallet
    public String createWallet() throws Exception {
        String path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
        String fileName = WalletUtils.generateLightNewWalletFile("password", new File(path));
        return path + "/" + fileName;
    }

    /* Load the new wallet file once it is created
     *  Replace the File path with newly created wallet file path
     */
    public Credentials loadCredentials(String password, String fileName) throws Exception {

        Credentials credentials = WalletUtils.loadCredentials(
                password,
                fileName);
        return credentials;
    }

    public String sendTransaction(Web3j web3, String from, String contractAddress, Credentials credentials, String ether) throws Exception {
        BigInteger amountWei = Convert.toWei(ether, Convert.Unit.ETHER).toBigInteger();
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = getNonce(from, web3);


        RawTransaction transaction = RawTransaction.createEtherTransaction(nonce, GAS_PRICE_DEFAULT, GAS_LIMIT_DEFAULT, contractAddress, amountWei);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        org.web3j.protocol.core.methods.response.EthSendTransaction transactionResponse = web3.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = transactionResponse.getTransactionHash();

        return transactionHash;
    }
    private static BigInteger getNonce(String address, Web3j web3) throws InterruptedException, ExecutionException {
        EthGetTransactionCount txCount = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                .sendAsync().get();
        BigInteger nonce = txCount.getTransactionCount();
        return nonce;
    }

}

