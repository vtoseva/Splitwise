package bg.sofia.uni.fmi.mjt.splitwise.manager;

import bg.sofia.uni.fmi.mjt.splitwise.entity.Transaction;
import java.nio.channels.SocketChannel;
import java.util.List;

public interface ActionAPI {
    String addFriend(String friendUsername, SocketChannel sc);

    String createGroup(String groupName, List<String> groupUsernames, SocketChannel sc);

    String splitGroup(double amount, String groupName, String reason, SocketChannel sc);

    String payed(double amount, String friendUsername, SocketChannel sc);

    String getStatus(SocketChannel sc);

    String seeAll(SocketChannel sc);

    // user can see payments they made
    String getPaymentsHistory(SocketChannel sc);

    String help();

    String getNotifications(SocketChannel sc);

    // NOT IMPLEMENTED
    // after a transaction is made the server can check if the participants are currently logged and send them
    // a notification
    String sendNotificationToLoggedUsers(Transaction transaction);
}
