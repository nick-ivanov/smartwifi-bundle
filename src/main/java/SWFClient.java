import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class SWFClient {

    public static void main(String[] args) throws Exception {

        SWFLogger.separator("hashchain");

        System.out.println(SWFHelper.getFullHashChain("hello", 10));

        ClientSecret secret = SWFHelper.generateClientSecret();

        SWFLogger.log("hashchain", "secret class object", secret.toString());

        System.out.println(secret);

        String walletfile = SWFHelper.getUserProperty("wallet_file");
        String walletpassword = SWFHelper.getUserProperty("wallet_password");
        Credentials credentials = WalletUtils.loadCredentials(walletpassword, walletfile);

        //System.out.println("CREDENTIALS: " + credentials.toString());

        String port = SWFHelper.getUserProperty("port");

        // [[ SEND: TRANSMISSION #0 ]]
        try (Socket socket = new Socket("localhost", Integer.parseInt(port))) {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String wallet_public = SWFHelper.getUserProperty("wallet_public");

            // [[ RECEIVE: TRANSMISSION #1 ]]
            System.out.println(in.nextLine());

            // [[ SEND: TRANSMISSION #2 ]]
            out.println("hello:" + wallet_public + ":" + secret.getTop());


            // [[ RECEIVE: TRANSMISSION #3 ]]
            String reply = in.nextLine();
            reply = reply.replaceAll("\n", "");
            String[] aaa = reply.split(":");

            System.out.println("REPLY: " + Arrays.asList(aaa));

            String infura_api_key = SWFHelper.getUserProperty("infura_api_key");

            Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/" + infura_api_key));  // defaults to http://localhost:8545/


            //Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/walletfile");

            /*TransactionReceipt transactionReceipt = Transfer.sendFunds(
                    web3, credentials, aaa[1],
                    BigDecimal.valueOf(0.01), Convert.Unit.ETHER).send(); */


            //Wallet wallet = new Wallet();

            String contract = aaa[1];
            String price = aaa[2];

            String max_price = SWFHelper.getUserProperty("max_price");

            if(Double.parseDouble(price) > Double.parseDouble(max_price)) {
                out.println("RIPOFF");
                out.println("bye");
                socket.close();
                return;
            }

            SWFHelper.contractFund(web3, wallet_public, contract, credentials, price);

            //String result = wallet.sendTransaction(web3, wallet_public, contract, credentials, price);

            // [[ SEND: TRANSMISSION #4 ]]
            out.println("OK");


            // MESSAROUND


//            List<Type> inputParameters = new ArrayList<>();
//            List<TypeReference<?>> outputParameters = new ArrayList<>();
//
//
//            outputParameters.add(new TypeReference<Utf8String>() {});
//
//            Function function = new Function("renderHelloWorld",
//                    inputParameters,
//                    outputParameters);
//            String functionEncoder = FunctionEncoder.encode(function);
//
//            EthCall response = web3.ethCall(Transaction.createEthCallTransaction("0xe57EB9F6f71B4f9F37e6aB730942851d067d402E","0x03f081e292a6645d5bbd2017ca035175cd6a0b05", functionEncoder), DefaultBlockParameterName.LATEST).sendAsync().get();
//
////            Type resault = someType.get(0);
////
////            String a = resault.toString();
//
//
//
//            List<Type> someType = FunctionReturnDecoder.decode(response.getValue(),function.getOutputParameters());
//            Iterator<Type> it = someType.iterator();
//            Type resault = someType.get(0);
//            String a = resault.toString();
//
//            System.out.println("Hello0: " + a);
//            System.out.println("Hello0.1: " + response.toString());
//            System.out.println("Hello1: " + response.getValue());
//            System.out.println("Hello2: " + response.getResult());
//            System.out.println("Hello3: " + response.getRawResponse());



            // END-OF-MESSAROUND


            // MESSAROUND 1

//            System.out.println("blah: " + secret.getTop().replaceAll("0x", ""));
//
//
//
//
//            List<Type> inputParameters = new ArrayList<>();
//            inputParameters.add(new Bytes32(Numeric.hexStringToByteArray(secret.getTop().replaceAll("0x", ""))));
//
//
//            List<TypeReference<?>> outputParameters = new ArrayList<>();
//
//
//            //outputParameters.add(new TypeReference<Utf8String>() {});
//
//            Function function = new Function("setHash",
//                    inputParameters,
//                    outputParameters);
//            String functionEncoder = FunctionEncoder.encode(function);
//
//            EthCall response = web3.ethCall(Transaction.createEthCallTransaction("0xe57EB9F6f71B4f9F37e6aB730942851d067d402E","0xfbb8784d5980c19ce5c9e2d7fcc793b3176b1718", functionEncoder), DefaultBlockParameterName.LATEST).sendAsync().get();
//
////            Type resault = someType.get(0);
////
////            String a = resault.toString();
//
//
//
//            //List<Type> someType = FunctionReturnDecoder.decode(response.getValue(),function.getOutputParameters());
//            //Iterator<Type> it = someType.iterator();
//            //Type resault = someType.get(0);
//            //String a = resault.toString();
//
//
//            //This guy: 7e5fd2767e1a8499bfc8672ef9c9d7d0265c5a965978ee7bed78ea705f44b11b
//
//            //System.out.println("Hello0: " + a);
//            System.out.println("Hello0.1: " + response.toString());
//            System.out.println("Hello1: " + response.getValue());
//            System.out.println("Hello2: " + response.getResult());
//            System.out.println("Hello3: " + response.getRawResponse());

            // END OF MESSAROUND 1


            // MESSAROUND 1.5

//            System.out.println("top: " + secret.getTop());
//
//            List<Type> inputParameters = new ArrayList<>();
//            inputParameters.add(new Bytes32(Numeric.hexStringToByteArray(secret.getTop().replaceAll("0x", ""))));
//
//
//            List<TypeReference<?>> outputParameters = new ArrayList<>();
//
//
//            //outputParameters.add(new TypeReference<Utf8String>() {});
//
//            Function function = new Function("setHash",
//                    inputParameters,
//                    outputParameters);
//
//            String encodedFunction = FunctionEncoder.encode(function);
//
//            BigInteger gasPrice = BigInteger.valueOf(200000000000L);
//            BigInteger gasLimit = BigInteger.valueOf(5000000L);
//            //BigInteger nonce = null;
//            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
//                    "0xe57EB9F6f71B4f9F37e6aB730942851d067d402E", DefaultBlockParameterName.LATEST).send();
//            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
//
//
//            Transaction transaction = Transaction.createFunctionCallTransaction(
//                    "0xe57EB9F6f71B4f9F37e6aB730942851d067d402E", nonce, gasPrice, gasLimit, "0xfbb8784d5980c19ce5c9e2d7fcc793b3176b1718", encodedFunction);
//
//
//            RawTransaction rawTransaction  = RawTransaction.createTransaction(
//                    nonce, gasPrice, gasLimit, "0xfbb8784d5980c19ce5c9e2d7fcc793b3176b1718", encodedFunction);
//
//            //org.web3j.protocol.core.methods.response.EthSendTransaction transactionResponse =
//              //      web3.ethSendTransaction(transaction).sendAsync().get();
//
//            System.out.println("transaction: " + rawTransaction.getData());
//            //System.out.println("transaction: " + rawTransaction.);
//            System.out.println("transaction: " + rawTransaction.toString());
//            //System.out.println("transaction: " + rawTransaction.getData());
//
//
//            if(rawTransaction == null) {
//                System.out.println("raw transaction is null");
//            }
//
//            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//            String hexValue = Numeric.toHexString(signedMessage);
//            EthSendTransaction rawTrasactionResponse = web3.ethSendRawTransaction(hexValue).send();
//
//
//            //org.web3j.protocol.core.methods.response.EthSendTransaction rawTrasactionResponse = web3.ethSendRawTransaction("0x" + rawTransaction.getData()).sendAsync().get();
//
//            //String transactionHash = transactionResponse.getTransactionHash();
//
//
//            String rawTransactionHash = rawTrasactionResponse.getTransactionHash();
//
//            System.out.println("Transaction hash: " + rawTransactionHash);
//            System.out.println("tr.toString(): " + rawTrasactionResponse.toString());
//            System.out.println("tr.getJsonrpc(): " + rawTrasactionResponse.getJsonrpc());
//            System.out.println("tr.getRawResponse(): " + rawTrasactionResponse.getRawResponse());
//            System.out.println("tr.getResult(): " + rawTrasactionResponse.getResult());
//            System.out.println("tr.hasError(): " + rawTrasactionResponse.hasError());
//            //System.out.println("tr.getError(): " + rawTrasactionResponse.getError().getMessage());
//
//
////            System.out.println("Transaction hash: " + transactionHash);
////            System.out.println("tr.toString(): " + transactionResponse.toString());
////            System.out.println("tr.getJsonrpc(): " + transactionResponse.getJsonrpc());
////            System.out.println("tr.getRawResponse(): " + transactionResponse.getRawResponse());
////            System.out.println("tr.getResult(): " + transactionResponse.getResult());
////            System.out.println("tr.hasError(): " + transactionResponse.hasError());
////            System.out.println("tr.getError(): " + transactionResponse.getError().getMessage());



            //SWFHelper.contractSetHash(web3, credentials, "0xe57EB9F6f71B4f9F37e6aB730942851d067d402E", "0xfbb8784d5980c19ce5c9e2d7fcc793b3176b1718", secret.getTop());

            //System.out.println("hash setting: " + secret.getTop());

            //Thread.sleep(60000);


            //System.out.println("hash returned: " + SWFHelper.contractGetHash(web3, "0xe57EB9F6f71B4f9F37e6aB730942851d067d402E", "0xfbb8784d5980c19ce5c9e2d7fcc793b3176b1718"));



            // END OF MESSAROUND 1.5


            // MESSAROUND 2

//            List<Type> inputParameters1 = new ArrayList<>();
//            List<TypeReference<?>> outputParameters1 = new ArrayList<>();
//
//
//            outputParameters1.add(new TypeReference<Bytes32>() {});
//
//            Function function1 = new Function("getHash",
//                    inputParameters1,
//                    outputParameters1);
//            String functionEncoder1 = FunctionEncoder.encode(function1);
//
//            EthCall response = web3.ethCall(Transaction.createEthCallTransaction("0xe57EB9F6f71B4f9F37e6aB730942851d067d402E","0xfbb8784d5980c19ce5c9e2d7fcc793b3176b1718", functionEncoder1), DefaultBlockParameterName.LATEST).sendAsync().get();
//
////            Type resault = someType.get(0);
////
////            String a = resault.toString();
//
//
//
//            List<Type> someType = FunctionReturnDecoder.decode(response.getValue(),function1.getOutputParameters());
//            Iterator<Type> it = someType.iterator();
//            Type resault = someType.get(0);
//            Bytes32 a = (Bytes32) resault;
//
//
//            System.out.println("Hello0: " + Numeric.toHexString(((Bytes32) resault).getValue()));
//            System.out.println("Hello0.0.1: " + Numeric.toHexString(a.getValue()) );
//            System.out.println("Hello0.1: " + response.toString());
//            System.out.println("Hello1: " + response.getValue());
//            System.out.println("Hello2: " + response.getResult());
//            System.out.println("Hello3: " + response.getRawResponse());

            // END OF MESSAROUND 2


            try {
                // [[ RECEIVE: TRANSMISSION #5 ]]
                reply = in.nextLine();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            reply = reply.replaceAll("\n", "");



            if(!reply.equals("GO")) {
                System.out.println("Nogo");
                out.println("bye");
                socket.close();
                return;
            }

            long timestamp = SWFHelper.getTimestamp();
            String line = "";


            System.out.println("Start loop");

            int chainsize = Integer.parseInt(SWFHelper.getUserProperty("chainsize"));
            int subperiod_msec = Integer.parseInt(SWFHelper.getUserProperty("subperiod_msec"));

            ArrayList<String> fullHashChain = SWFHelper.getFullHashChain(secret.getSeed(), 60);
            Collections.reverse(fullHashChain);

            SWFHelper.startDupset();

            SWFHelper.nbSleep(10000);

            double speed_min = Double.parseDouble(SWFHelper.getUserProperty("min_speed_mbps"));

            for(int i = 0; i < chainsize; i++) {

                if(SWFHelper.speed >= speed_min) {
                    out.println("ack:" + fullHashChain.get(i));
                } else {
                    out.println("TOOSLOW");
                }

                try {
                    reply = in.nextLine();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                reply = reply.replaceAll("\n", "");

                if(!reply.equals("GOTCHA")) {
                    System.out.println("QUIT");
                    out.println("bye");
                    socket.close();
                    return;
                }

                /* if(i == 6) {
                    SWFHelper.nbSleep(subperiod_msec * 2);
                } */
                
                SWFHelper.nbSleep(subperiod_msec);
            }

        }
    }
}