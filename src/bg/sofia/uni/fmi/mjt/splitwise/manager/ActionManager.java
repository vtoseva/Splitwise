package bg.sofia.uni.fmi.mjt.splitwise.manager;

import bg.sofia.uni.fmi.mjt.splitwise.entity.Group;
import bg.sofia.uni.fmi.mjt.splitwise.entity.Transaction;
import bg.sofia.uni.fmi.mjt.splitwise.entity.TransactionType;
import bg.sofia.uni.fmi.mjt.splitwise.entity.UserProfile;
import bg.sofia.uni.fmi.mjt.splitwise.exception.DuplicateGroupNameException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.GroupNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.InternalErrorException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.splitwise.repository.TransactionRepo;
import bg.sofia.uni.fmi.mjt.splitwise.repository.UserRepo;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

public class ActionManager implements ActionAPI {
    private static ActionManager instance = new ActionManager();

    private ActionManager() {
    }

    public static ActionManager getInstance() {
        return instance;
    }

    @Override
    public String addFriend(String friendUsername, SocketChannel sc) {
        String username;
        try {
            username = UserActionManager.getInstance().getUsername(sc);
        } catch (NotLoggedInException | InternalErrorException e) {
            return e.getMessage();
        }

        if (friendUsername == null || friendUsername.isBlank()) {
            return MessageConstants.NULL_ARG_ERROR_MSG;
        }

        if (!UserRepo.getInstance().contains(friendUsername)) {
            return MessageConstants.USER_NOT_FOUND_ERROR_MSG + friendUsername;
        }

        // add friendship
        Group newFriendship = new Group(MessageConstants.FRIENDSHIP_DEF_NAME, Set.of(username, friendUsername));
        try {
            UserRepo.getInstance().get(username).addGroup(newFriendship);
            UserRepo.getInstance().get(friendUsername).addGroup(newFriendship);
        } catch (DuplicateGroupNameException e) {
            return MessageConstants.ALREADY_FRIEND_ERROR_MSG;
        }

        return String.format(MessageConstants.ADD_FRIEND_SUCCESS_MSG, friendUsername);
    }

    @Override
    public String createGroup(String groupName, List<String> groupUsernames, SocketChannel sc) {
        String username;
        try {
            username = UserActionManager.getInstance().getUsername(sc);
        } catch (NotLoggedInException | InternalErrorException e) {
            return e.getMessage();
        }

        UserProfile user = UserRepo.getInstance().get(username);

        if (groupName == null || groupName.isBlank()) {
            return MessageConstants.NULL_ARG_ERROR_MSG;
        }

        if (!groupName.matches(MessageConstants.GROUP_NAME_PATTERN)) {
            return MessageConstants.INVALID_GROUP_NAME_ERROR_MSG;
        }

        for (String name : groupUsernames) {
            if (!nullValidation(name)) {
                return MessageConstants.NULL_ARG_ERROR_MSG;
            } else if (!UserRepo.getInstance().contains(name)) {
                return MessageConstants.USER_NOT_FOUND_ERROR_MSG + name;
            }
        }

        Set<String> members = new HashSet<>(groupUsernames);
        members.add(username);

        Group newGroup = new Group(groupName, Set.copyOf(groupUsernames));
        try {
            user.addGroup(newGroup);
        } catch (DuplicateGroupNameException e) {
            return e.getMessage();
        }

        return MessageConstants.NEW_GROUP_SUCCESS_MSG;
    }

    @Override
    public String splitGroup(double amount, String groupName, String reason, SocketChannel sc) {
        String username;
        try {
            username = UserActionManager.getInstance().getUsername(sc);
        } catch (NotLoggedInException | InternalErrorException e) {
            return e.getMessage();
        }
        UserProfile user = UserRepo.getInstance().get(username);

        if (amount <= 0) {
            return MessageConstants.INVALID_SUM_ERROR_MSG;
        }

        if (!nullValidation(groupName, reason)) {
            return MessageConstants.NULL_ARG_ERROR_MSG;
        }

        Group group;
        try {
            group = user.getGroup(groupName);
        } catch (GroupNotFoundException e) {
            return e.getMessage();
        }

        // create new transaction
        Transaction newTransaction =
            new Transaction(TransactionType.SPLIT, LocalDateTime.now(), username, group, reason, amount);
        TransactionRepo.getInstance().addTransaction(newTransaction);

        // recalculate balance
        user.pay(group.usernames(), amount, group.usernames().size());

        StringBuilder response =
            new StringBuilder(String.format(MessageConstants.SPLIT_SUCCESS_MSG, amount, groupName));

        for (var name : group.usernames()) {
            if (!username.equals(name)) {
                appendBalanceStatusToResponse(response, user, name);
            }
        }

        return response.toString();
    }

    @Override
    public String payed(double amount, String friendUsername, SocketChannel sc) {
        String username;
        try {
            username = UserActionManager.getInstance().getUsername(sc);
        } catch (NotLoggedInException | InternalErrorException e) {
            return e.getMessage();
        }

        if (amount <= 0) {
            return MessageConstants.INVALID_SUM_ERROR_MSG;
        }

        if (!nullValidation(friendUsername)) {
            return MessageConstants.NULL_ARG_ERROR_MSG;
        }

        UserProfile user = UserRepo.getInstance().get(username);

        // create new transaction
        Transaction newTransaction;
        try {
            newTransaction = new Transaction(TransactionType.PAYED, LocalDateTime.now(), friendUsername,
                user.getGroup(friendUsername), null, amount);
        } catch (GroupNotFoundException e) {
            return e.getMessage();
        }
        TransactionRepo.getInstance().addTransaction(newTransaction);

        // recalculate balance
        UserRepo.getInstance().get(friendUsername).pay(Set.of(username), amount, MessageConstants.PAY_SPLIT);
        user.getPayed(friendUsername, amount, MessageConstants.PAY_SPLIT);

        StringBuilder response =
            new StringBuilder(String.format(MessageConstants.PAYED_SUCCESS_MSG, friendUsername, amount));
        response.append(System.lineSeparator()).append(MessageConstants.CURRENT_STATUS_MSG);

        appendBalanceStatusToResponse(response, user, friendUsername);

        return response.toString();
    }

    @Override
    public String getStatus(SocketChannel sc) {
        String username;
        try {
            username = UserActionManager.getInstance().getUsername(sc);
        } catch (NotLoggedInException | InternalErrorException e) {
            return e.getMessage();
        }

        UserProfile user = UserRepo.getInstance().get(username);

        StringBuilder response = new StringBuilder();

        if (user.balance().isEmpty()) {
            return MessageConstants.NOTHING_TO_SHOW_MSG;
        }

        for (var name : user.balance().keySet()) {
            if (user.balance().get(name) > 0) {
                response.append(String.format(MessageConstants.OWES_YOU_MSG, name, user.getBalanceWith(name)));
            } else if (user.balance().get(name) < 0) {
                response.append(String.format(MessageConstants.YOU_OWE_MSG, name, -user.getBalanceWith(name)));
            }
            response.append(System.lineSeparator());
        }

        return response.toString();
    }

    @Override
    public String seeAll(SocketChannel sc) {
        String username;
        try {
            username = UserActionManager.getInstance().getUsername(sc);
        } catch (NotLoggedInException | InternalErrorException e) {
            return e.getMessage();
        }

        Collection<Group> relations = UserRepo.getInstance().get(username).relations().values();

        List<Group> friends = new ArrayList<>();
        List<Group> groups = new ArrayList<>();

        for (var r : relations) {
            if ((r.usernames().size() == MessageConstants.FRIEND_GROUP_SIZE)) {
                friends.add(r);
            } else {
                groups.add(r);
            }
        }

        if (friends.isEmpty() && groups.isEmpty()) {
            return MessageConstants.NOTHING_TO_SHOW_MSG;
        }

        StringBuilder response = new StringBuilder();
        response.append(System.lineSeparator());

        if (!friends.isEmpty()) {
            response.append(MessageConstants.FRIENDS);
            appendNamesToResponse(response, username, friends);
        }

        if (!groups.isEmpty()) {
            response.append(MessageConstants.GROUPS);
            appendNamesToResponse(response, username, groups);
        }

        return response.toString();
    }

    @Override
    public String getPaymentsHistory(SocketChannel sc) {
        String username;
        try {
            username = UserActionManager.getInstance().getUsername(sc);
        } catch (NotLoggedInException | InternalErrorException e) {
            return e.getMessage();
        }

        List<Transaction> transactions = TransactionRepo.getInstance().get(username);

        if (transactions.isEmpty()) {
            return MessageConstants.NOTHING_TO_SHOW_MSG;
        }

        StringBuilder response = new StringBuilder();

        for (var t : transactions) {
            if (t.type().equals(TransactionType.PAYED)) {
                response.append(MessageConstants.PAYMENT_MSG);
            } else {
                if (t.group().name().equals(MessageConstants.FRIENDSHIP_DEF_NAME)) {
                    String friend = null;
                    for (var name : t.group().usernames()) {
                        if (!name.equals(username)) {
                            friend = name;
                        }
                    }
                    response.append(
                        String.format(MessageConstants.SPLIT_MSG, t.time(), t.amount(), friend, t.reason()));
                } else {
                    response.append(
                        String.format(MessageConstants.SPLIT_MSG, t.time(), t.amount(), t.group().name(),
                            t.reason()));
                }
            }
            response.append(System.lineSeparator());
        }

        return response.toString();
    }

    @Override
    public String help() {
        return MessageConstants.HELP;
    }

    @Override
    public String getNotifications(SocketChannel sc) {
        String username;
        try {
            username = UserActionManager.getInstance().getUsername(sc);
        } catch (NotLoggedInException | InternalErrorException e) {
            return e.getMessage();
        }

        // from lastLogout to now
        LocalDateTime lastLogout = UserRepo.getInstance().get(username).getLastLogoutTime();

        if (lastLogout == null) {
            return System.lineSeparator() + MessageConstants.NOTHING_TO_SHOW_MSG;
        }

        Set<Transaction> transactions = TransactionRepo.getInstance().getTransactions();

        List<Transaction> friendsNotifications = new ArrayList<>();
        Map<String, List<Transaction>> groupNotifications = new HashMap<>();

        for (var t : transactions) {
            if (t.time().isAfter(lastLogout) && t.group().usernames().contains(username)) {
                if (t.group().usernames().size() == MessageConstants.FRIEND_GROUP_SIZE) {
                    friendsNotifications.add(t);
                } else {
                    groupNotifications.putIfAbsent(t.group().name(), new ArrayList<>());
                    groupNotifications.get(t.group().name()).add(t);
                }
            }
        }

        StringBuilder response = new StringBuilder();
        response.append(System.lineSeparator());
        if (friendsNotifications.isEmpty() && groupNotifications.isEmpty()) {
            return MessageConstants.NOTHING_TO_SHOW_MSG;
        }
        response.append(MessageConstants.NOTIFICATIONS);

        if (!friendsNotifications.isEmpty()) {
            response.append(System.lineSeparator()).append(MessageConstants.FRIENDS);
        }

        for (var t : friendsNotifications) {
            response.append(generateNotification(username, t)).append(System.lineSeparator());
        }

        if (!groupNotifications.isEmpty()) {
            response.append(System.lineSeparator()).append(MessageConstants.GROUPS);
        }

        for (String groupName : groupNotifications.keySet()) {
            response.append(System.lineSeparator()).append(String.format("* %s:", groupName));
            for (var t : groupNotifications.get(groupName)) {
                response.append(generateNotification(username, t)).append(System.lineSeparator());
            }
            response.append(System.lineSeparator());
        }

        return response.toString();
    }

    @Override
    public String sendNotificationToLoggedUsers(Transaction transaction) {
        // NOT IMPLEMENTED
        // for every logged participant send a notification
        return null;
    }

    private String generateNotification(String username, Transaction transaction) {
        if (transaction.type().equals(TransactionType.PAYED)) {
            String friend = null;
            for (var p : transaction.group().usernames()) {
                if (!p.equals(username)) {
                    friend = p;
                }
            }

            return String.format(MessageConstants.APPROVE_NOTIFICATION_MSG, friend, transaction.amount());
        } else {
            return String.format(MessageConstants.OWE_NOTIFICATION_MSG, transaction.from(),
                UserRepo.getInstance().get(transaction.from()).getBalanceWith(username), transaction.reason());
        }
    }

    private void appendNamesToResponse(StringBuilder response, String username, List<Group> groups) {
        for (var group : groups) {
            Set<String> users = new HashSet<>(group.usernames());
            users.remove(username);

            if (!group.name().equals(MessageConstants.FRIENDSHIP_DEF_NAME)) {
                response.append(group.name()).append(":").append(System.lineSeparator());
            }
            for (var name : users) {
                response.append(name).append(System.lineSeparator());
            }
        }
    }

    private void appendBalanceStatusToResponse(StringBuilder response, UserProfile user, String name) {
        double currentBalance = user.getBalanceWith(name);
        response.append(System.lineSeparator());

        if (currentBalance < 0) {
            response.append(String.format(MessageConstants.YOU_OWE_MSG, name, -currentBalance));
        } else if (currentBalance > 0) {
            response.append(String.format(MessageConstants.OWES_YOU_MSG, name, currentBalance));
        } else {
            response.append(MessageConstants.CLEAR_BALANCE_MSG);
        }
    }

    private boolean nullValidation(String... args) {
        for (var arg : args) {
            if (arg == null || arg.isBlank()) {
                return false;
            }
        }
        return true;
    }

}
