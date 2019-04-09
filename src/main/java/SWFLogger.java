import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SWFLogger {
    static final String logdir = "/home/nick/mega/research/smartwifi/src/main/log";

    static void separator(String logger) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String right_now = dateFormat.format(date);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logdir + "/" + logger + ".log", true));
            writer.append("*********************************************************************\n");
            writer.append("************************ " + right_now + " **************************\n");
            writer.append("*********************************************************************\n\n");
            writer.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    static void log(String logger, String what, String stuff) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String right_now = dateFormat.format(date);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logdir + "/" + logger + ".log", true));
            writer.append("==== new entry: " + right_now + " (" + what + ") ====\n");
            writer.append(stuff);
            writer.append("\n===================== (end of entry) ======================\n\n");
            writer.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }


}
