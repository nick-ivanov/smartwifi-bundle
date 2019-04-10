/*
The server design is inspired by: https://gist.github.com/dannvix/5385384
 */

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


class SWFServerWorker extends Thread {
    private Socket client;

    private boolean doneHello = false;

    private String clientIp;
    private String clientWallet;
    private String clientChainTop;
    private Web3j web3;

    private volatile boolean goodAck;
    private volatile long lastAckTime;
    private volatile boolean internetActivated;


    SWFServerWorker(Socket client) {
        this.client = client;
    }

    String getIpAddress() {
        InetAddress ip = null;
        String hostname = "";

        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);

        } catch (UnknownHostException e) {

            e.printStackTrace();
        }

        return ip.getHostAddress();
    }

    private void responseHello(String[] aaa, PrintWriter writer, BufferedReader reader) {
        System.out.println("HELLO detected");

        if(aaa.length != 3) {
            System.out.println("OOPS: HELLO should have exactly two arguments.");
            return;
        }

        System.out.println("WALLET: " + aaa[1]);
        System.out.println("HASHCHAIN TOP: " + aaa[2]);

        clientIp = client.getInetAddress().getHostAddress();
        clientWallet = aaa[1];
        clientChainTop = aaa[2];

        // Check here whether the values are good or not

        String contract = SWFHelper.getServerProperty("contract_address");
        String price = SWFHelper.getServerProperty("price");


        String server_private_key = SWFHelper.getServerProperty("wallet_private");

        Credentials credentials1 = Credentials.create(server_private_key);


        SWFHelper.contractSetHash(web3, credentials1, credentials1.getAddress(), contract, clientChainTop);
        System.out.println("hash setting: " + clientChainTop);
        try { Thread.sleep(60000); } catch(Exception ex) { ex.printStackTrace(); }
        System.out.println("hash returned: " + SWFHelper.contractGetHash(web3, credentials1.getAddress(), contract));

        SWFHelper.contractSetAddress(web3, credentials1, credentials1.getAddress(), contract, clientWallet);
        System.out.println("address setting: " + clientWallet);
        try { Thread.sleep(60000); } catch(Exception ex) { ex.printStackTrace(); }
        System.out.println("address returned: " + SWFHelper.contractGetAddress(web3, credentials1.getAddress(), contract));

        // [[ SEND: TRANSMISSION #3 ]]
        writer.println("contract:" + contract + ":" + price);

        String line = "";

        try {
            // [[ RECEIVE: TRANSMISSION #4 ]]
            line = reader.readLine();
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        if (!line.trim().equals("OK")) {
            writer.println("bye!");
            return;
        }

        BigInteger wei;
        double balance = 0.0;

        double priced = Double.parseDouble(price);

        int count = 0;

        int max_fund_wait = Integer.parseInt(SWFHelper.getServerProperty("max_fund_wait"));

        do {
            try {
                EthGetBalance ethGetBalance = web3
                        .ethGetBalance(contract, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

                wei = ethGetBalance.getBalance();

                double weid = wei.doubleValue();
                balance = weid / 1000000000000000000.0;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);

            }

            count ++;

            if(count > max_fund_wait) {
                writer.println("WHATEVER");
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }

        } while(balance < priced);

        System.out.println("Balance: " + balance);

        // [[ SEND: TRANSMISSION #5 ]]
        writer.println("GO");

        goodAck = true;
        lastAckTime = SWFHelper.getTimestamp();
        internetActivated = false;

        Runnable task = () -> {
            while(true) {

                long elapsed = SWFHelper.getTimestamp() - lastAckTime;

                if(!client.isConnected()) {
                    System.out.println("Client disconnected");
                    break;
                }

                if(!client.isBound()) {
                    System.out.println("Client is not bound");
                    break;
                }



                if(client.isClosed()) {
                    System.out.println("Client is closed");
                    break;
                }

                if(client.isInputShutdown()) {
                    System.out.println("Client input shutdown");
                    break;
                }

                if(client.isOutputShutdown()) {
                    System.out.println("Client output shutdown");
                    break;
                }

                if(elapsed > 60) {
                    goodAck = false;
                }

                if (goodAck) {
                    if(!internetActivated) {
                        SWFHelper.activateInet(clientIp);
                        internetActivated = true;
                    }
                } else {
                    if(internetActivated) {
                        SWFHelper.deactivateInet(clientIp);
                        internetActivated = false;
                    }
                }

                if(internetActivated) {
                    System.out.println("[inet-activated]");
                } else {
                    System.out.println("[inet-deactivated]");
                }

                SWFHelper.nbSleep(1000);
            }
        };

        Thread worker = new Thread(task);
        worker.start();

        int chainsize = Integer.parseInt(SWFHelper.getServerProperty("chainsize"));
        int subperiod_msec = Integer.parseInt(SWFHelper.getServerProperty("subperiod_msec"));

        System.out.println("chainsize: " + chainsize);
        System.out.println("subperiod_msec: " + subperiod_msec);

        count = -1;

        long timestamp = SWFHelper.getTimestamp();
        long elapsed;

        String last_good_ack = "";
        int last_ack_number = 0;

        for(int i = 0; i < 2 * chainsize; i++) {

            //SWFHelper.nbSleep(subperiod_msec);

            try {
                // [[ RECEIVE: TRANSMISSION #6,8,... ]]
                line = reader.readLine();
            } catch(Exception ex) {
                ex.printStackTrace();
                break;
            }

            elapsed = SWFHelper.getTimestamp() - timestamp;

            System.out.println("LINE: " + line);
            System.out.println("ELAPSED: " + elapsed);
            aaa = line.split(":");

            if(aaa[0].equals("ack")) {
                if(SWFHelper.verifyHash(clientChainTop, aaa[1], count)) {
                    goodAck = true;
                    writer.println("GOTCHA");
                    count++;
                    last_good_ack = aaa[1];
                    last_ack_number = count;
                    lastAckTime = SWFHelper.getTimestamp();

                    if(count >= chainsize) {
                        break;
                    }
                } else {
                    goodAck = false;
                    writer.println("NOHASHNOSERVICE");
                }
            }

//            if (!line.trim().equals("OK")) {
//                writer.println("bye!");
//                return;
//            }


        }

        System.out.println("last_good_ack: " + last_good_ack);
        System.out.println("last_ack_number: " + last_ack_number);

        try {
            String walletfile = SWFHelper.getServerProperty("wallet_file");
            String walletpassword = SWFHelper.getServerProperty("wallet_password");

            Credentials credentials = WalletUtils.loadCredentials(walletpassword, walletfile);

            System.out.println("check credentials: " + credentials.getAddress());
            System.out.println("check credentials1: " + credentials1.getAddress());

            System.out.println("contract: " + contract);

            String res = SWFHelper.contractPay(web3, credentials1, credentials1.getAddress(), contract, last_good_ack, last_ack_number-1);
            if(res == null) {
                System.out.println("pay() faiiled");
            } else {
                System.out.println("pay() succeeded with transaction id: " + res);
            }

        } catch(Exception ex) {
            ex.printStackTrace();

        }


        worker.interrupt();
    }


    public void run () {
        String infura_api_key = SWFHelper.getServerProperty("infura_api_key");


        try {
            web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/" + infura_api_key));
        } catch (Exception ex) {
            System.out.println("web3j error");
            System.out.println(ex.getMessage());
            return;
        }


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

            // [[ SEND: TRANSMISSION #1 ]]
            writer.println("SmartWiFi Router welcomes you!");

            //while (true) {
                String line = reader.readLine();

                if (line.trim().equals("bye")) {
                    writer.println("BYE");
                    return;
                }

                String[] aaa = line.split(":");

                if(aaa[0].equals("hello")) {
                    responseHello(aaa, writer, reader);
                }

//                if(aaa[0].equals("OK")) {
//                    System.out.println("GOT OK1");
//                }

                //writer.println("[echo] " + line);
            //}
        }
        catch (Exception e) {
            System.err.println("Exception caught: client disconnected.");
            System.err.println("Details: " + e.getMessage());
            System.err.println("More details: ");
            e.printStackTrace();
        }
        finally {
            try { client.close(); }
            catch (Exception e ){ e.printStackTrace(); }
        }
    }
}

public class SWFServer {

    public static void main (String[] args) {

        System.out.println(SWFHelper.getServerProperty("ip"));

        int chainsize = Integer.parseInt(SWFHelper.getServerProperty("chainsize"));

        System.out.printf("Chain size: %d\n", chainsize);


        String port = SWFHelper.getServerProperty("port");

        try {
            ServerSocket server = new ServerSocket(Integer.parseInt(port));
            while (true) {
                // [[ RECEIVE: TRANSMISSION #0 ]]
                Socket client = server.accept();
                SWFServerWorker handler = new SWFServerWorker(client);
                System.out.println("ADDRESS: " + client.getInetAddress().getHostAddress());
                handler.start();
            }
        } catch (Exception e) {
            System.err.println("Exception caught:" + e);
            System.err.println("Details: " + e.getMessage());
            System.err.println("More details: ");
            e.printStackTrace();
        }
    }
}