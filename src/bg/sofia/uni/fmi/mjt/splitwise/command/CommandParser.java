package bg.sofia.uni.fmi.mjt.splitwise.command;

import java.nio.channels.SocketChannel;

public class CommandParser {
    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String LOGOUT = "logout";
    private static final String ADD_FRIEND = "add-friend";
    private static final String CREATE_GROUP = "create-group";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String PAYED = "payed";
    private static final String GET_STATUS = "get-status";
    private static final String SEE_ALL = "see-all";
    private static final String GET_HISTORY = "get-history";
    private static final String HELP = "help";
    private static final int TOKEN_COUNT_ONE = 1;
    private static final int TOKEN_COUNT_TWO = 2;
    private static final int TOKEN_COUNT_THREE = 3;
    private static final int TOKEN_COUNT_FOUR = 4;

    private CommandParser() {
    }

    public static Command of(String input, SocketChannel sc) {
        String[] tokens = input.split(" ");

        return switch (tokens[0]) {
            case HELP -> new HelpCommand(sc, tokens);
            case REGISTER ->
                (tokens.length != TOKEN_COUNT_THREE) ? new UnknownCommand(sc, tokens) : new RegisterCommand(sc, tokens);
            case LOGIN ->
                (tokens.length != TOKEN_COUNT_THREE) ? new UnknownCommand(sc, tokens) : new LoginCommand(sc, tokens);
            case LOGOUT ->
                (tokens.length != TOKEN_COUNT_ONE) ? new UnknownCommand(sc, tokens) : new LogoutCommand(sc, tokens);
            case ADD_FRIEND ->
                (tokens.length != TOKEN_COUNT_TWO) ? new UnknownCommand(sc, tokens) : new AddFriendCommand(sc, tokens);
            case CREATE_GROUP -> (tokens.length < TOKEN_COUNT_FOUR) ? new UnknownCommand(sc, tokens) :
                new CreateGroupCommand(sc, tokens);
            case SPLIT, SPLIT_GROUP ->
                (tokens.length != TOKEN_COUNT_FOUR) ? new UnknownCommand(sc, tokens) : new SplitCommand(sc, tokens);
            case PAYED ->
                (tokens.length != TOKEN_COUNT_THREE) ? new UnknownCommand(sc, tokens) : new PayedCommand(sc, tokens);
            case GET_STATUS ->
                (tokens.length != TOKEN_COUNT_ONE) ? new UnknownCommand(sc, tokens) : new GetStatusCommand(sc, tokens);
            case SEE_ALL ->
                (tokens.length != TOKEN_COUNT_ONE) ? new UnknownCommand(sc, tokens) : new SeeAllCommand(sc, tokens);
            case GET_HISTORY ->
                (tokens.length != TOKEN_COUNT_ONE) ? new UnknownCommand(sc, tokens) : new GetHistoryCommand(sc, tokens);
            default -> new UnknownCommand(sc, tokens);
        };
    }
}
