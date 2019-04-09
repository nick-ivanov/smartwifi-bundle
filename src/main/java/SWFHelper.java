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
import org.web3j.utils.Numeric;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;


public class SWFHelper {
    final static String serverConfig = "/home/nick/mega/research/smartwifi/src/main/resources/server.properties";
    final static String userConfig = "/home/nick/mega/research/smartwifi/src/main/resources/user.properties";

    static volatile double speed;

    static String getUserProperty(String property) {
        return getProperty(userConfig, property);
    }

    static String getServerProperty(String property) {
        return getProperty(serverConfig, property);
    }

    static String getProperty(String domain, String property) {
        try {
            Properties prop = new Properties();

            try {
                FileReader file = new FileReader(domain);
                prop.load(new FileReader(domain));
                file.close();
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
                System.exit(1);
            }

            String value = prop.getProperty(property);
            return value;
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
            System.exit(1);
        }

        return "";
    }

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
        String res = SWFHelper.getHashChainFromBytes(byte_hash, n);

//        System.out.println("n: " + n);
//        System.out.println("verifyHash: top: " + top);
//        System.out.println("verifyHash: res: " + res);
//        System.out.println("verifyHash: hash: " + hash);
//        System.out.println("checking whether " + res + " == " + top);


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
        int chainsize = Integer.parseInt(SWFHelper.getUserProperty("chainsize"));
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


        //outputParameters.add(new TypeReference<Utf8String>() {});

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

//        System.out.println("Transaction hash: " + rawTrasactionResponse.getTransactionHash());
//        System.out.println("tr.toString(): " + rawTrasactionResponse.toString());
//        System.out.println("tr.getJsonrpc(): " + rawTrasactionResponse.getJsonrpc());
//        System.out.println("tr.getRawResponse(): " + rawTrasactionResponse.getRawResponse());
//        System.out.println("tr.getResult(): " + rawTrasactionResponse.getResult());
//        System.out.println("tr.hasError(): " + rawTrasactionResponse.hasError());
//
//        if(rawTrasactionResponse.hasError()) {
//            System.out.println("tr.error: " + rawTrasactionResponse.getError());
//            System.out.println("tr.error: " + rawTrasactionResponse.getError().getMessage());
//        }

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

        System.out.println("Addressy: " + a.toString());
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

    static void dupsetProbe() {

        final int buf_len = 1024 * 1024;
        final int small_buf_len = 1024;
        final int num_sites = 10;


        ArrayList<String> sites = new ArrayList<>();


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

//        char * buf = (char *) malloc (BUF_LEN);
//        if (buf == NULL) {
//            fprintf (stderr, "malloc() error\n");
//            return 1;
//        }


        String buf_small;

//        char * buf_small = (char *) malloc (SMALL_BUF_LEN);
//        if (buf_small == NULL) {
//            fprintf (stderr, "malloc() error\n");
//            return 1;
//        }

        double res_maxspeed;
        long res_delay;
        long res_payload;
        double res_avg_latency;
        double sma2, sma3, sma4, sma5, sma6;
        double lsma2, lsma3, lsma4, lsma5, lsma6;
        double tcma;

        //double speeds[60];
        //double lspeeds[60];

        double[] speeds = new double[60];
        double[] lspeeds = new double[60];



        for(int k = 0; k < 120; k++) {

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

            //printf("Number of sites: %d\n", NUM_SITES);

            //char* data;

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

                //printf("ORIG. DELTA: %d\n", delta);

                //sleep(2);

                time3 = getTimestampMillis();

                buf_small = getSmallPage(sites.get(i));
                time4 = getTimestampMillis();

                if(buf_small == null) delta = 0.0;
                else delta -= buf_small.length();

                //printf("ADJ. DELTA: %d\n", delta);

                pload_sum += delta;


                long tdelta = (time2 - time1) - (time4 - time3);

                double speed;
                if(tdelta <= 0) {
                    speed = 0.0;
                } else {
                    speed = (double) ( (delta * 8.0) / (tdelta / 1000.0));
                }

                //printf("Site #%d (%s) payload delta (bytes): %d, delay delta (ms): %ld, speed (bps): %f, latency component (ms): %ld\n", (i+1), sites[i], delta, tdelta, speed, time4-time3);

                sum_latency += (time4 - time3);

                speedsum += speed;

                if(speed > speedmax) {
                    speedmax = speed;
                }
            }

            time5 = getTimestampMillis();

            //double speedavg = (double) (speedsum / (double) nsites);
            //printf("AVERAGE SPEED (bps): %f\n", speedavg);

            //printf("MAXIMUM SPEED (mbps): %f\n", speedmax/1000000.0);

            //printf("SPEED MEASUREMENT DELAY (msec): %ld\n", time5 - time0);
            //printf("TOTAL PAYLOAD SIZE (bytes): %ld\n", pload_sum);

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
                //printf("adding %f to SMA2\n", speeds[z]);
            }
            sma2 = sma_sum/y;
            lsma2 = lsma_sum/y;

            sma_sum = 0.0;
            lsma_sum = 0.0;
            y = 0.0;
            for(int z = k; z >= 0 && y < 3; z--, y++) {
                sma_sum += speeds[z];
                lsma_sum += lspeeds[z];
                //printf("adding %f to SMA3\n", speeds[z]);
            }
            sma3 = sma_sum/y;
            lsma3 = lsma_sum/y;


            sma_sum = 0.0;
            lsma_sum = 0.0;
            y = 0.0;
            for(int z = k; z >= 0 && y < 4; z--, y++) {
                sma_sum += speeds[z];
                lsma_sum += lspeeds[z];
                //printf("adding %f to SMA4\n", speeds[z]);
            }
            sma4 = sma_sum/y;
            lsma4 = lsma_sum/y;

            sma_sum = 0.0;
            lsma_sum = 0.0;
            y = 0.0;
            for(int z = k; z >= 0 && y < 5; z--, y++) {
                sma_sum += speeds[z];
                lsma_sum += lspeeds[z];
                //printf("adding %f to SMA5\n", speeds[z]);
            }
            sma5 = sma_sum/y;
            lsma5 = lsma_sum/y;


            sma_sum = 0.0;
            lsma_sum = 0.0;
            y = 0.0;
            for(int z = k; z >= 0 && y < 6; z--, y++) {
                sma_sum += speeds[z];
                lsma_sum += lspeeds[z];
                //printf("adding %f to SMA6\n", speeds[z]);
            }
            sma6 = sma_sum/y;
            lsma6 = lsma_sum/y;


            sma_sum = 0.0;

            //printf("k = %d\n", k);
            for(int z = k; z >= 0; z--) {
                sma_sum += speeds[z];
                //printf(".");
                //printf("adding %f to SMA6\n", speeds[z]);
            }
            //printf("\n");

            tcma = sma_sum/(k+1);


            // MAXSPEED, DELAY, PAYLOAD, AVG_LATENCY,SMA2,SMA3,SMA4,SMA5,SMA6,LSMA2,LSMA3,LSMA4,LSMA5,LSMA6

            //AVGLATENCY,SMA2,SMA3,SMA4,SMA5,SMA6,LSMA2,LSMA3,LSMA4,LSMA5,LSMA6


            //AVGLATENCY,SMA2,SMA3,SMA4,SMA5,SMA6,LSMA2,LSMA3,LSMA4,LSMA5,LSMA6
            //System.out.printf("%f,%ld,%ld,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f\n", res_maxspeed, res_delay, res_payload, res_avg_latency, sma2, sma3, sma4, sma5, sma6, lsma2, lsma3, lsma4, lsma5, lsma6, tcma);

            // free(rand_websites);
            // free(rand_indices);

            speed = tcma;

            System.out.println("SPEED_REPORT: " + tcma);
            //SWFHelper.nbSleep(1000);
        }
    }

    static void startDupset() {
        Runnable task = () -> {
            dupsetProbe();
        };

        Thread worker = new Thread(task);
        worker.start();
    }
}
