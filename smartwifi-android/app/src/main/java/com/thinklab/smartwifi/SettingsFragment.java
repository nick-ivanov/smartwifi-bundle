package com.thinklab.smartwifi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.view.View.VISIBLE;

public class SettingsFragment extends Fragment {
    private TextView walletAddress;
    private EditText privateInput;
    private TextView walletPrivate;
    private Button saveBttn;
    private EditText infuraText;
    private TextView infuraLabel;
    private Button saveBttn2;
    private SeekBar seekBar;
    private TextView seekbarLabel;
    private static int pval;
    private double speedMin;

    //public Credentials credentials;
    //Wallet wallet = new Wallet();
    Web3j web3j;
    static public String privateKey;
    static public String publicKey;
    static public String addr;
    static public String infura;
    static public Credentials cs;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        walletAddress = (TextView) view.findViewById(R.id.walletAddress2);
        //walletPrivate = (TextView) view.findViewById(R.id.walletPrivateKey2);
        privateInput = (EditText) view.findViewById(R.id.PrivateInput);
        saveBttn = (Button) view.findViewById(R.id.saveBttn);
        infuraText = (EditText) view.findViewById(R.id.infuraInput);
        saveBttn2 = (Button) view.findViewById(R.id.saveBttn2);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekbarLabel = (TextView) view.findViewById(R.id.seekBarLabelPost);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String privateCheck = preferences.getString("Private", "None");
        String publicKey2 = preferences.getString("Public", "");
        String addressCheck = preferences.getString("Address", "");
        String infuraKey = preferences.getString("Infura", "None");

        if(privateCheck != "None"){
            cs = Credentials.create(privateCheck.toString());
            setWalletInfo(cs, privateCheck, publicKey2, addressCheck);
            privateKey = cs.getEcKeyPair().getPrivateKey().toString(16);
            publicKey = cs.getEcKeyPair().getPublicKey().toString(16);
            addr = cs.getAddress();
        }
        if(infuraKey != "None") {
            setInfuraInfo(infuraKey);
            infura = infuraKey;
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ConnectionFragment f = (ConnectionFragment) getFragmentManager().findFragmentByTag("connection");
                if(pval == 0){
                    seekbarLabel.setText("No Speed Control");
                    speedMin = 0.0;
                    f.setSpeed_min(0.0);

                }
                if(pval == 1){
                    seekbarLabel.setText("Low (1 Mbps)");
                    speedMin = 1.0;
                    Log.d("Calling connection", "Calling connection");
                    f.setSpeed_min(1.0);
                }
                if(pval == 2){
                    seekbarLabel.setText("Medium (2 Mbps)");
                    Log.d("Calling connection", "Calling connection");
                    speedMin = 2.0;
                    f.setSpeed_min(2.0);
                }
                if(pval == 3){
                    seekbarLabel.setText("High (4 Mbps)");
                    Log.d("Calling connection", "Calling connection");
                    speedMin = 4.0;
                    f.setSpeed_min(4.0);
                }
                if(pval == 4){
                    seekbarLabel.setText("Highest (8 Mbps)");
                    Log.d("Calling connection", "Calling connection");
                    speedMin = 8.0;
                    f.setSpeed_min(8.0);
                }
            }
        });


        saveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                privateKey = privateInput.getText().toString();
                cs = Credentials.create(privateKey);

                privateKey = cs.getEcKeyPair().getPrivateKey().toString(16);
                publicKey = cs.getEcKeyPair().getPublicKey().toString(16);
                addr = cs.getAddress();
                setWalletInfo(cs, privateKey, publicKey, addr);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Private",privateKey);
                editor.putString("Address", addr);
                editor.putString("Public", publicKey);
                editor.apply();



            }
            });

        saveBttn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infura = infuraText.getText().toString();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Infura" , infura);
                editor.apply();
            }
        });

        return view;
    }







    public void setWalletInfo(Credentials cs , String privateKey, String publicKey, String addr){
        walletAddress.setText(addr);
        ConnectionFragment f = (ConnectionFragment) getFragmentManager().findFragmentByTag("connection");
        String temp1 = addr.substring(0,8);
        String temp2 = addr.substring(addr.length() - 6);
        addr = temp1 + "..." + temp2;
        f.setWallet(addr);

    }

    public void setInfuraInfo(String infuraInfo) {
        ConnectionFragment f = (ConnectionFragment) getFragmentManager().findFragmentByTag("connection");
        f.setInfuraInfo(infuraInfo);
    }

    public String getWalletAddress(){
        return addr;
    }

    public double getSpeed_min(){
        return speedMin;
    }

    public Credentials getCredentials(){
        return cs;
    }

    public String getWalletPrivate(){
        return privateKey;
    }




}
