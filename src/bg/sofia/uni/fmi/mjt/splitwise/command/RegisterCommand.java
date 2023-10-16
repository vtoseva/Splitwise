package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.UserActionManager;

import java.nio.channels.SocketChannel;
import java.util.List;

public class RegisterCommand extends Command {
    private static final int USERNAME_TOKEN = 1;
    private static final int PASSWORD_TOKEN = 2;
    RegisterCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        // todo: make const tokens 1 and 2
        return UserActionManager.getInstance().register(args[USERNAME_TOKEN], args[PASSWORD_TOKEN], sc);
    }
}