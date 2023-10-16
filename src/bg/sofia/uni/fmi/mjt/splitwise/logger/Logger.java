package bg.sofia.uni.fmi.mjt.splitwise.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Logger {
    private static final String LOG_PATH = "resources/logs.txt";

    public static void log(LocalDateTime time, String message) {
        try {
            FileWriter fileWriter = new FileWriter(LOG_PATH, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(time.toString() + " | " + message);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
