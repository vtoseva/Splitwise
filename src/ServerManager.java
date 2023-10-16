import bg.sofia.uni.fmi.mjt.splitwise.Server;

import java.util.Scanner;

public class ServerManager {
    public static void main(String[] args) throws InterruptedException {
        final int port = 9000;
        Server server = new Server(port);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();

            if (line.equals("start")) {
                server.load();
                server.start();
            } else if (line.equals("stop")) {
                server.save();
                server.stopRunning();
                server.join();
                break;
            }
        }
    }
}