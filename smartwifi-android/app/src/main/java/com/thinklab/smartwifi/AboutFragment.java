package com.thinklab.smartwifi;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Provider;
import java.security.Security;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.web3j.crypto.Credentials;

import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.protocol.core.methods.response.EthGetCode;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.web3j.crypto.Credentials;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class AboutFragment extends Fragment {
    private Button button;
    EthGetBalance ethGetBalance = null;
    Wallet wallet = new Wallet();
    Web3j web3j;
    public Credentials credentials;
    public BigInteger wei;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupBouncyCastle();
        //checkPermissions();
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {EthGetBalance ethGetBalance = null;

            @Override
            public void onClick(View v) {
                HttpService infuraHttpService = new HttpService("https://ropsten.infura.io/v3/cdc0f13f944348f9a395ad4887d1e859");
                web3j = Web3j.build(infuraHttpService); //connect to infura with web3j
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            File f = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath());
                            Collection collection = getListOfAllConfigFiles(f);
                            String fileName2 = collection.iterator().next().toString();
                            Log.d("Filename2: ", fileName2);
                            credentials = wallet.loadCredentials("password", fileName2);
                            Log.d("Loaded", "successfully");
                            String address = credentials.getAddress();
                            Log.d("Address", address);
                            ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
                            Log.d("Balance recieved: ", ethGetBalance.toString());
                            wei = ethGetBalance.getBalance();
                            Log.d("Balance: ", wei.toString());
                            Looper.prepare();
                            if (Double.parseDouble(wei.toString()) < .03) {
                                //Toast.makeText(getContext(), "Balance is too low", Toast.LENGTH_LONG);
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast success = Toast.makeText(getActivity(), "Balance too low", Toast.LENGTH_LONG);
                                        success.setGravity(Gravity.CENTER, 0, 0);
                                        success.show();
                                    }
                                });
                                Log.d("Error", "Balance too low");
                            }
                            else {
                                Credentials credentials = wallet.loadCredentials("password", "/storage/emulated/0/Download/hey.json");
                                wallet.sendTransaction(web3j, "0xc7722426fd46467ecDB7650345337EeCaAF9aeB9", "0xF88dF2c638ec22B422Ab519f07636a9e47f470df", credentials, ".02");
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast fail = Toast.makeText(getActivity(), "Sending to contract..", Toast.LENGTH_LONG);
                                        fail.setGravity(Gravity.CENTER, 0, 0);
                                        fail.show();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            }
        });



        return view;
    }

    Collection<String> getListOfAllConfigFiles(File directory)
    {
        return FileUtils.listFiles(directory, new WildcardFileFilter("*.json"), null);
    }



    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }


}
