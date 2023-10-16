package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.UserActionManager;

import java.nio.channels.SocketChannel;

public class LoginCommand extends Command {
    private static final int USERNAME_TOKEN = 1;
    private static final int PASSWORD_TOKEN = 2;
    LoginCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        return UserActionManager.getInstance().login(args[USERNAME_TOKEN], args[PASSWORD_TOKEN], sc);
    }
}
