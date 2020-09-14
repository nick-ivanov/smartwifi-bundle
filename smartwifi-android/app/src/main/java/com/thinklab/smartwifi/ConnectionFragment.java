package com.thinklab.smartwifi;


import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Chronometer;
import android.widget.Button;
import android.os.SystemClock;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.jsoup.Jsoup;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.UUID;

import static java.lang.String.valueOf;

public class ConnectionFragment extends Fragment {
    public static ConnectionFragment C1;
    private TextView connectionBool;
    private TextView ipAddress;
    private Button button1;
    private static TextView smartContractAddress;
    private static TextView connectionSpeed;
    private TextView etherWallet;
    private static Chronometer timetotalChrono;
    private static Chronometer simpleChronometer;
    private TextView wifissid;
    private static TextView totalCharge;
    private static int flag = 0;
    private static FragmentActivity mInstance;
    private static long timeWhenStopped = 0;
    private static TextView smartStatus;
    private static volatile boolean flag2 = true;
    private static volatile boolean dupset_flag = true;
    private static volatile boolean thread2 = true;
    private static Thread thread;
    private static Thread worker;
    private static volatile double speed_min;
    private static Socket socket;
    private static String infura_api_key;
            //"ef1ce1202d8248a69d1eacd3a6237f28"; //Might need to replace!


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_connection, container, false);
        mInstance = getActivity();
        etherWallet = (TextView) view.findViewById(R.id.message92);
        timetotalChrono = (Chronometer) view.findViewById(R.id.simpleChronometer1);
        timetotalChrono.setBase(SystemClock.elapsedRealtime());
        totalCharge = (TextView) view.findViewById(R.id.message82);
        simpleChronometer = (Chronometer) view.findViewById(R.id.simpleChronometer);
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        smartContractAddress = (TextView) view.findViewById(R.id.message112);
        smartStatus = (TextView) view.findViewById(R.id.message122);
        wifissid = (TextView) view.findViewById(R.id.message02);
        wifissid.setText("");

        connectionBool = (TextView) view.findViewById(R.id.messageActive);
        button1 = (Button) view.findViewById(R.id.button1);
        ipAddress = (TextView) view.findViewById(R.id.message22);
        connectionSpeed = (TextView) view.findViewById(R.id.message32);
        setRetainInstance(true);
        connectionBool.setText("Dissconnected");
        button1.setVisibility(View.GONE);
        connectionSpeed.setTextColor(Color.GREEN);
        totalCharge.setText("0.000 ETH");
        smartContractAddress.setText("");
        connectionSpeed.setText("N/A");
        ipAddress.setText("");
        smartStatus.setText("");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConnectedStatus(false);
                button1.setVisibility(View.GONE);
                setConnectionClock(false);
                setIpAddress(false, "");
                smartContractAddress.setText("");
                totalCharge.setText("0.000 ETH");
                connectionSpeed.setText("N/A");
                ipAddress.setText("N/A");
                dupset_flag = false;
                flag2 = false;
                try{
                    socket.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
                connectionSpeed.setText("N/A");


            }
        });


        return view;
    }
    public void setConnectedStatus(boolean connected) {
        if(connected) {
            connectionBool.setText("Active");
            connectionBool.setTextColor(Color.GREEN);
            flag2 = true;

        }
        else{
            connectionBool.setText("Dissconnected");
            connectionBool.setTextColor(Color.BLACK);
        }
    }
    public static void setInfuraInfo(String infuraInfo){
        infura_api_key = infuraInfo;
    }


    public static void setSpeed(Double speedTmp){
        speed = speedTmp;
        Log.d("speed is ", valueOf(speed));

    }

    public static void setSpeed_min(Double speed_min2){
        speed_min = speed_min2;
        Log.d("speed was changed", "speed was changed");
    }

    public void showDisconnect(boolean show){
        button1.setVisibility(View.VISIBLE);
    }

    public void setSSID(String ssid){
        wifissid.setText(ssid);
    }

    public void setWallet(String addr){
        this.etherWallet.setText(addr);
    }

    public void setIpAddress(boolean connected, String ip){
        if(connected){
            ipAddress.setText(ip);
            ipAddress.setTextColor(Color.GREEN);
        }
        else{
            ipAddress.setText("IP: NONE");
            ipAddress.setTextColor(0xFFBCBCBC);
        }
    }


    public void setConnectionClock(boolean connected) {

        if(connected){
            simpleChronometer.setBase(SystemClock.elapsedRealtime());
            timetotalChrono.setBase(SystemClock.elapsedRealtime());
            simpleChronometer.start();
            timetotalChrono.start();
            //updateTime("0.0000 ETH");
        }
        else {
            simpleChronometer.setBase(SystemClock.elapsedRealtime());
            timetotalChrono.setBase(SystemClock.elapsedRealtime());
            simpleChronometer.stop();
            timetotalChrono.stop();
            //timerHandler.removeCallbacks(updater);

        }

    }



        static volatile double speed;

        static String getHashChain(String seed, int n) {
            Keccak.DigestKeccak kecc = new Keccak.Digest256();
            byte[] input = seed.getBytes();
            kecc.update(input, 0, input.length);

            byte[] current = kecc.digest();

            for(int i = 0; i < n; i++) {
                byte[] prev = current;

                kecc.update(prev, 0, prev.length);
                current = kecc.digest();
            }

            System.out.println("CURRENT:: " + Arrays.toString(current));

            return Numeric.toHexString(current);
        }

        static String getHashChainFromBytes(byte[] input, int n) {
            Keccak.DigestKeccak kecc = new Keccak.Digest256();
            kecc.update(input, 0, input.length);

            byte[] current = kecc.digest();

            for(int i = 0; i < n; i++) {
                byte[] prev = current;

                kecc.update(prev, 0, prev.length);
                current = kecc.digest();
            }

            return Numeric.toHexString(current);
        }

        static boolean verifyHash(String top, String hash, int n) {
            if(n == -1) {
                return true;
            }

            byte[] byte_hash = Numeric.hexStringToByteArray(hash.replaceAll("0x", ""));
            String res = getHashChainFromBytes(byte_hash, n);



            if(res.equals(top)) {
                return true;
            }

            return false;
        }

        static ClientSecret generateClientSecret() {
            ClientSecret secret = new ClientSecret();

            UUID uuid = UUID.randomUUID();
            String seed = uuid.toString();
            secret.setSeed(seed);
            int chainsize = 60;
            secret.setSize(chainsize);
            ArrayList<String> hchain = getFullHashChain(seed, chainsize);
            secret.setHashchain(hchain);
            secret.setTop(hchain.get(59));

            return secret;
        }

        static ArrayList<String> getFullHashChain(String seed, int n) {
            ArrayList<String> hashes = new ArrayList<>();
            Keccak.DigestKeccak kecc = new Keccak.Digest256();
            byte[] input = seed.getBytes();
            kecc.update(input, 0, input.length);
            byte[] current = kecc.digest();

            for(int i = 0; i < n; i++) {
                byte[] prev = current;

                kecc.update(prev, 0, prev.length);
                current = kecc.digest();
                hashes.add(Numeric.toHexString(current));
            }

            return hashes;
        }

        static void nbSleep(long subperiod_msec) {
            try {
                Thread.sleep(subperiod_msec);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        static void activateInternet(String ip) {
            System.out.println("Activating Internet for IP: " + ip);
        }

        static void deactivateInternet(String ip) {
            System.out.println("Deactivating Internet for IP: " + ip);
        }

        // Couresy: https://stackoverflow.com/a/8263301/8041645
        static long getTimestamp() {
            Log.d("Calling ", "Get time stamp");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.clear();
            calendar.setTime(new Date());
            return calendar.getTimeInMillis() / 1000L;
        }


        static long getTimestampMillis() {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.clear();
            calendar.setTime(new Date());
            return calendar.getTimeInMillis();
        }


        static String contractGetHash(Web3j web3, String from, String contract) {
            List<Type> inputParameters1 = new ArrayList<>();
            List<TypeReference<?>> outputParameters1 = new ArrayList<>();


            outputParameters1.add(new TypeReference<Bytes32>() {});

            Function function1 = new Function("getHash",
                    inputParameters1,
                    outputParameters1);
            String functionEncoder1 = FunctionEncoder.encode(function1);


            EthCall response = null;

            try {
                response = web3.ethCall(Transaction.createEthCallTransaction(from, contract, functionEncoder1), DefaultBlockParameterName.LATEST).sendAsync().get();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            List<Type> someType = FunctionReturnDecoder.decode(response.getValue(),function1.getOutputParameters());
            Iterator<Type> it = someType.iterator();
            Type resault = someType.get(0);
            Bytes32 a = (Bytes32) resault;

            return response.getValue();
        }

        static String contractSetHash(Web3j web3, Credentials credentials, String from, String contract, String hash) {
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Bytes32(Numeric.hexStringToByteArray(hash.replaceAll("0x", ""))));

            List<TypeReference<?>> outputParameters = new ArrayList<>();

            Function function = new Function("setHash",
                    inputParameters,
                    outputParameters);

            String encodedFunction = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount;
            BigInteger nonce;

            try {
                ethGetTransactionCount = web3.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).send();
                nonce = ethGetTransactionCount.getTransactionCount();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            RawTransaction rawTransaction  = RawTransaction.createTransaction(
                    nonce, Wallet.GAS_PRICE_DEFAULT, Wallet.GAS_LIMIT_DEFAULT, contract, encodedFunction);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction rawTrasactionResponse;


            try {
                rawTrasactionResponse = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            return rawTrasactionResponse.getTransactionHash();
        }

        static String contractFund(Web3j web3, String wallet_public, String contract, Credentials credentials, String price) {
            Wallet wallet = new Wallet();
            try {
                return wallet.sendTransaction(web3, wallet_public, contract, credentials, price);
            } catch(Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        static String contractPay(Web3j web3, Credentials credentials, String from, String contract, String hash, int n) {
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Bytes32(Numeric.hexStringToByteArray(hash.replaceAll("0x", ""))));

            BigInteger k = new BigInteger(String.valueOf(n));
            inputParameters.add(new Uint(k));

            List<TypeReference<?>> outputParameters = new ArrayList<>();



            Function function = new Function("pay",
                    inputParameters,
                    outputParameters);

            String encodedFunction = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount;
            BigInteger nonce;

            try {
                ethGetTransactionCount = web3.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).send();
                nonce = ethGetTransactionCount.getTransactionCount();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            RawTransaction rawTransaction  = RawTransaction.createTransaction(
                    nonce, Wallet.GAS_PRICE_DEFAULT, Wallet.GAS_LIMIT_DEFAULT, contract, encodedFunction);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction rawTrasactionResponse;

            try {
                rawTrasactionResponse = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            String transactionHash = null;
            int timeout = 5;
            while (null == transactionHash && timeout != 0) {
                transactionHash = rawTrasactionResponse.getTransactionHash();
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                timeout--;
            }



            return rawTrasactionResponse.getTransactionHash();
        }


        static String contractRefund(Web3j web3, Credentials credentials, String from, String contract, String hash) {
            List<Type> inputParameters = new ArrayList<>();
            inputParameters.add(new Bytes32(Numeric.hexStringToByteArray(hash.replaceAll("0x", ""))));

            List<TypeReference<?>> outputParameters = new ArrayList<>();


            Function function = new Function("refund",
                    inputParameters,
                    outputParameters);

            String encodedFunction = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount;
            BigInteger nonce;

            try {
                ethGetTransactionCount = web3.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).send();
                nonce = ethGetTransactionCount.getTransactionCount();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            RawTransaction rawTransaction  = RawTransaction.createTransaction(
                    nonce, Wallet.GAS_PRICE_DEFAULT, Wallet.GAS_LIMIT_DEFAULT, contract, encodedFunction);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction rawTrasactionResponse;

            try {
                rawTrasactionResponse = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            String transactionHash = null;
            int timeout = 5;
            while (null == transactionHash && timeout != 0) {
                transactionHash = rawTrasactionResponse.getTransactionHash();
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                timeout--;
            }


            return rawTrasactionResponse.getTransactionHash();
        }


        static String contractRefund(Web3j web3, Credentials credentials, String from, String contract, String hash, int n) {

            return null;
        }

        static String contractSetAddress(Web3j web3, Credentials credentials, String from, String contract, String address) {
            List<Type> inputParameters = new ArrayList<>();

            inputParameters.add(new Address(address));

            List<TypeReference<?>> outputParameters = new ArrayList<>();

            Function function = new Function("setAddress",
                    inputParameters,
                    outputParameters);

            String encodedFunction = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount;
            BigInteger nonce;

            try {
                ethGetTransactionCount = web3.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).send();
                nonce = ethGetTransactionCount.getTransactionCount();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            RawTransaction rawTransaction  = RawTransaction.createTransaction(
                    nonce, Wallet.GAS_PRICE_DEFAULT, Wallet.GAS_LIMIT_DEFAULT, contract, encodedFunction);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction rawTrasactionResponse;

            try {
                rawTrasactionResponse = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            return rawTrasactionResponse.getTransactionHash();
        }




        static String contractGetAddress(Web3j web3, String from, String contract) {
            List<Type> inputParameters1 = new ArrayList<>();
            List<TypeReference<?>> outputParameters1 = new ArrayList<>();

            outputParameters1.add(new TypeReference<Address>() {});

            Function function1 = new Function("getAddress",
                    inputParameters1,
                    outputParameters1);
            String functionEncoder1 = FunctionEncoder.encode(function1);


            EthCall response = null;

            try {
                response = web3.ethCall(Transaction.createEthCallTransaction(from, contract, functionEncoder1), DefaultBlockParameterName.LATEST).sendAsync().get();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            List<Type> someType = FunctionReturnDecoder.decode(response.getValue(),function1.getOutputParameters());
            Iterator<Type> it = someType.iterator();
            Type resault = someType.get(0);
            Address a = (Address) resault;

            System.out.println("Address: " + a.toString());
            System.out.println("Another: " + a.toUint160().toString());

            return response.getValue();
        }


        static String getPage(String address) {

            try {
                return Jsoup.connect("http://" + address + ":85/payload10k.txt").get().html();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }


        }

        static String getSmallPage(String address) {
            try {
                return Jsoup.connect("http://" + address + ":85/oldpayload.txt").get().html();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

        }


        public static void dupsetProbe() {

            final int buf_len = 1024 * 1024;
            final int small_buf_len = 1024;
            final int num_sites = 10;


            ArrayList<String> sites = new ArrayList<>();

            Log.d("Adding sites", "sdfsd");

            sites.add("157.230.151.37");
            sites.add("178.62.109.110");
            sites.add("149.28.242.163");
            sites.add("207.148.87.126");
            sites.add("202.182.105.19");
            sites.add("68.183.89.82");
            sites.add("165.227.40.213");
            sites.add("188.166.87.44");
            sites.add("45.63.21.94");
            sites.add("68.183.236.197");

            String buf;
            String buf_small;



            double res_maxspeed;
            long res_delay;
            long res_payload;
            double res_avg_latency;
            double sma2, sma3, sma4, sma5, sma6;
            double lsma2, lsma3, lsma4, lsma5, lsma6;
            double tcma;


            double[] speeds = new double[60];
            double[] lspeeds = new double[60];


            Log.d("flag", valueOf(dupset_flag));
            for(int k = 0; k < 120 && dupset_flag; k++) {
                Log.d("Entered loop ", valueOf(dupset_flag));
                sma2 = 0.0;
                sma3 = 0.0;
                sma4 = 0.0;
                sma5 = 0.0;
                sma6 = 0.0;

                lsma2 = 0.0;
                lsma3 = 0.0;
                lsma4 = 0.0;
                lsma5 = 0.0;
                lsma6 = 0.0;

                tcma = 0.0;



                double speedsum = 0.0;


                String data;

                long time0 = getTimestampMillis();
                long pload_sum = 0;
                double speedmax = 0.0;
                double sum_latency = 0.0;
                double delta;

                long time1, time2, time3, time4, time5;

                int ret;

                for(int i = 0; i < num_sites; i++) {
                    time1 = getTimestampMillis();
                    buf = getPage(sites.get(i));


                    time2 = getTimestampMillis();

                    delta = 0;

                    delta = 0;
                    if(buf != null) {
                        delta = buf.length();
                    }


                    time3 = getTimestampMillis();

                    buf_small = getSmallPage(sites.get(i));
                    time4 = getTimestampMillis();

                    if(buf_small == null) delta = 0.0;
                    else delta -= buf_small.length();


                    pload_sum += delta;


                    long tdelta = (time2 - time1) - (time4 - time3);
                    double speed;
                    if(tdelta <= 0) {
                        speed = 0.0;
                    } else {
                        speed = (double) ( (delta * 8.0) / (tdelta / 1000.0));
                    }

                    sum_latency += (time4 - time3);

                    speedsum += speed;

                    if(speed > speedmax) {
                        speedmax = speed;
                    }
                }

                time5 = getTimestampMillis();


                res_maxspeed = speedmax/1000000.0;
                speeds[k] = res_maxspeed;
                res_delay = time5 - time0;
                res_payload = pload_sum;
                res_avg_latency = sum_latency / num_sites;

                lspeeds[k] = res_avg_latency;

                double sma_sum = 0.0;
                double lsma_sum = 0.0;
                double y = 0.0;
                for(int z = k; z >= 0 && y < 2; z--, y++) {
                    sma_sum += speeds[z];
                    lsma_sum += lspeeds[z];
                }
                sma2 = sma_sum/y;
                lsma2 = lsma_sum/y;
                sma_sum = 0.0;
                lsma_sum = 0.0;
                y = 0.0;
                for(int z = k; z >= 0 && y < 3; z--, y++) {
                    sma_sum += speeds[z];
                    lsma_sum += lspeeds[z];
                }
                sma3 = sma_sum/y;
                lsma3 = lsma_sum/y;

                sma_sum = 0.0;
                lsma_sum = 0.0;
                y = 0.0;
                for(int z = k; z >= 0 && y < 4; z--, y++) {
                    sma_sum += speeds[z];
                    lsma_sum += lspeeds[z];
                }
                sma4 = sma_sum/y;
                lsma4 = lsma_sum/y;
                sma_sum = 0.0;
                lsma_sum = 0.0;
                y = 0.0;
                for(int z = k; z >= 0 && y < 5; z--, y++) {
                    sma_sum += speeds[z];
                    lsma_sum += lspeeds[z];
                }
                sma5 = sma_sum/y;
                lsma5 = lsma_sum/y;

                sma_sum = 0.0;
                lsma_sum = 0.0;
                y = 0.0;
                for(int z = k; z >= 0 && y < 6; z--, y++) {
                    sma_sum += speeds[z];
                    lsma_sum += lspeeds[z];
                }
                sma6 = sma_sum/y;
                lsma6 = lsma_sum/y;
                sma_sum = 0.0;

                for(int z = k; z >= 0; z--) {
                    sma_sum += speeds[z];
                }
                tcma = sma_sum/(k+1);

                speed = tcma;
                Log.d("attempting", "attempting to display speed");

                try {
                    mInstance.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String speedtemp = String.format("%.2f", speed);
                            connectionSpeed.setText(speedtemp + " Mbps");
                            setSpeed(speed);
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("SPEED_REPORT: " + tcma);
                Log.d("speed report", valueOf(speed));

            }
        }

        public static void startDupset() {
            Runnable task = () -> {
                Log.d("running depsetProbe", "running");
                dupsetProbe();
            };

            worker = new Thread(task);
            worker.start();
        }


        public static double getSpeed() {
            return speed;
        }



    public static void run2() throws Exception {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    ClientSecret secret = generateClientSecret();
                    SettingsFragment settingsFragment = new SettingsFragment();
                    String privateKey = settingsFragment.getWalletPrivate();
                    String wallet_public = settingsFragment.getWalletAddress();
                    Credentials credentials = settingsFragment.getCredentials();
                    String port = "5566";
                    String server_host = "10.42.0.1";
                    // [[ SEND: TRANSMISSION #0 ]]

                    try {
                        socket = new Socket(server_host, Integer.parseInt(port));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        Scanner in = new Scanner(socket.getInputStream());
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                        //NEED TO REPLACE
                        // [[ RECEIVE: TRANSMISSION #1 ]]
                        System.out.println(in.nextLine());
                        //Log.d(in.nextLine(), "Transmission 1");
                        // [[ SEND: TRANSMISSION #2 ]]
                        out.println("hello:" + wallet_public + ":" + secret.getTop());
                        //Log.d("hello", wallet_public + ":" + secret.getTop());

                        // [[ RECEIVE: TRANSMISSION #3 ]]
                        String reply = in.nextLine();
                        reply = reply.replaceAll("\n", "");
                        String[] aaa = reply.split(":");
                        mInstance.runOnUiThread (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String temp1 = aaa[1].substring(0,8);
                                String temp2 = aaa[1].substring(aaa[1].length() - 6);
                                String addr = temp1 + "..." + temp2;
                                smartContractAddress.setText(addr);
                            }
                        }));

                        System.out.println("REPLY: " + Arrays.asList(aaa));
                        //Log.d("REPLY: " + Arrays.asList(aaa), "Recieved");

                        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/" + infura_api_key));  // defaults to http://localhost:8545/


                        String contract = aaa[1];
                        String price = aaa[2];

                        String max_price = "0.002";

                        if (Double.parseDouble(price) > Double.parseDouble(max_price)) {
                            out.println("RIPOFF");
                            out.println("bye");
                            socket.close();
                            return;
                        }

                        contractFund(web3, wallet_public, contract, credentials, price);

                        // [[ SEND: TRANSMISSION #4 ]]
                        out.println("OK");


                        try {
                            // [[ RECEIVE: TRANSMISSION #5 ]]
                            reply = in.nextLine();
                            if(reply.equals("GO")){
                                mInstance.runOnUiThread (new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    timetotalChrono.setBase(SystemClock.elapsedRealtime());
                                    timetotalChrono.start();
                                    simpleChronometer.setBase(SystemClock.elapsedRealtime());
                                    simpleChronometer.start();
                                    smartStatus.setText("Funded (0.072 ETH)");
                                }
                            }));
                        }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        reply = reply.replaceAll("\n", "");


                        if (!reply.equals("GO")) {
                            System.out.println("Nogo");
                            out.println("bye");
                            socket.close();
                            return;
                        }

                        long timestamp = getTimestamp();
                        String line = "";


                        System.out.println("Start loop");


                        int chainsize = 60;
                        int subperiod_msec = 60000;

                        ArrayList<String> fullHashChain = getFullHashChain(secret.getSeed(), 60);
                        Collections.reverse(fullHashChain);


                        startDupset();


                         nbSleep(10000);


                        int testdrive_periods = 2;

                        int count = 0;


                        for (int i = 0; i < chainsize * 2 && flag2 ; i++) {
                            if(!thread2){
                                thread.interrupt();
                            }

                            if (count >= chainsize) {
                                break;
                            }

                            if (getSpeed() >= speed_min || i < testdrive_periods) {
                                out.println("ack:" + fullHashChain.get(count));
                                count++;
                                double totalChargeTemp = count * 0.0012;
                                if(flag == 1) {
                                    mInstance.runOnUiThread(new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            simpleChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                                            simpleChronometer.start();
                                        }
                                    }));
                                    flag = 0;
                                }
                                else {
                                    mInstance.runOnUiThread(new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            totalCharge.setText(valueOf(totalChargeTemp) + " ETH");
                                        }
                                    }));
                                }

                            } else {
                                out.println("TOOSLOW");
                                mInstance.runOnUiThread (new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeWhenStopped = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
                                        simpleChronometer.stop();
                                    }
                                }));

                            }

                            try {
                                reply = in.nextLine();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            reply = reply.replaceAll("\n", "");

                            if (!reply.equals("GOTCHA")) {
                                flag = 1;
                                mInstance.runOnUiThread (new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeWhenStopped = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
                                        simpleChronometer.stop();
                                    }
                                }));
                            }


                            nbSleep(subperiod_msec);
                        }

                        try {
                            Thread.sleep(1800000);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        contractRefund(web3, credentials, wallet_public, contract, secret.getTop());
                    }

                }catch(Exception e){
                    Log.d("Error : ",e.getMessage());
                }
            }
        });
        thread.start();

    }



}
