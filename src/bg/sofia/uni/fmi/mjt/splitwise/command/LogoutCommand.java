package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.UserActionManager;

import java.nio.channels.SocketChannel;

public class LogoutCommand extends Command {
    LogoutCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        return UserActionManager.getInstance().logout(sc);
    }

}
