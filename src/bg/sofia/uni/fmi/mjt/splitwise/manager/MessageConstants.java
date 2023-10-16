package bg.sofia.uni.fmi.mjt.splitwise.manager;

public class MessageConstants {
    public static final int FRIEND_GROUP_SIZE = 2;
    public static final int PAY_SPLIT = 1;
    public static final String FRIENDSHIP_DEF_NAME = "###";
    public static final String FRIENDS = "Friends: ";
    public static final String GROUPS = "Groups: ";
    public static final String NOTIFICATIONS = "*** Notifications: ***";
    public static final String NEW_NOTIFICATION_MSG = "*** New notification: ***";
    public static final String NULL_ARG_ERROR_MSG = "Arguments should not be null";
    public static final String USER_NOT_FOUND_ERROR_MSG = "User not found: ";
    public static final String ADD_FRIEND_SUCCESS_MSG = "Friend with username %s was added successfully!";
    public static final String ALREADY_FRIEND_ERROR_MSG = "You are already friends!";
    public static final String NEW_GROUP_SUCCESS_MSG = "New group was created successfully!";
    public static final String GROUP_NAME_PATTERN = "^[a-zA-Z0-9!@$%^&*(),.?\":{}|<>_+=\\-\\[\\]\\\\/ ]{1,20}$";
    public static final String INVALID_GROUP_NAME_ERROR_MSG = "Group name is invalid!";
    public static final String INVALID_SUM_ERROR_MSG = "Sum can't be less or equal to 0";
    public static final String SPLIT_SUCCESS_MSG = "Split %s LV between you and %s";
    public static final String LOGIN_SUCCESS_MSG = "Successful login!";
    public static final String REGISTER_SUCCESS_MSG = "Successful registration!";
    public static final String ALREADY_LOGGED_ERROR_MSG = "Cannot login, you're already logged in as %s";
    public static final String PAYED_SUCCESS_MSG = "%s payed you %s LV.";
    public static final String CURRENT_STATUS_MSG = "Current status: ";
    public static final String OWES_YOU_MSG = "%s owes you %s LV";
    public static final String YOU_OWE_MSG = "%s: You owe %s LV";
    public static final String CLEAR_BALANCE_MSG = "Balance is clear";
    public static final String NOTHING_TO_SHOW_MSG = "Nothing to show.";
    public static final String PAYMENT_MSG = "%s: You payed %s %s LV";
    public static final String SPLIT_MSG = "%s: You split %s with %s LV for %s";
    public static final String APPROVE_NOTIFICATION_MSG = "%s approved your payment %s.";
    public static final String OWE_NOTIFICATION_MSG = "You owe %s %s LV [%s]";
    public static final String WRONG_PASSWORD_ERROR_MSG = "The username and password don't match";
    public static final String INTERNAL_ERROR_MSG =
        "The server has encountered an internal error and was unable to complete your request.";
    public static final String WEAK_PASSWORD_MSG =
        "Password should have at least 8 characters, one lowercase letter, " +
            "one uppercase letter, one digit, and one special character (!, @, #, $, %, &, *, +, -, ?, ^, _, ~, or `)";
    public static final String LOGOUT_MSG = "Logged out";
    public static final String INVALID_USERNAME_ERROR_MSG = "The username is invalid";
    public static final String NOT_LOGGED_ERROR_MSG =
        "You have to be logged in to use this feature. If you don't have an account you can register.";
    public static final String DUPLICATE_GROUP_NAME_ERROR_MSG = "Group with this name already exists";
    public static final String GROUP_NOT_FOUND_ERROR_MSG = "Group name was not found";
    public static final String PASSWORD_PATTERN =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&*+.\\-=?^_~])[a-zA-Z\\d!@#$%&*+.\\-=?^_~]{8,30}$";
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_.]{5,30}$";

    public static final String HELP = "Commands are:" + System.lineSeparator() +
        "register <username> <password>" + System.lineSeparator() +
        "login <username> <password>" + System.lineSeparator() +
        "logout" + System.lineSeparator() +
        "add-friend <friendName>" + System.lineSeparator() +
        "create-group <groupName> <user1> <user2> ... /at least 2 users/" + System.lineSeparator() +
        "split <amount> <friendName> <reason>" + System.lineSeparator() +
        "split-group <amount> <groupName> <reason>" + System.lineSeparator() +
        "payed <amount> <friendName> /approves payment/" + System.lineSeparator() +
        "get-status /shows obligations/" + System.lineSeparator() +
        "see-all /shows all friends and groups/" + System.lineSeparator() +
        "get-history /shows history of all payments/" + System.lineSeparator();
}
