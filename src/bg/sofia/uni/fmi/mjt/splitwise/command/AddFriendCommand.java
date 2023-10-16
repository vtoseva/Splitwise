package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.ActionManager;

import java.nio.channels.SocketChannel;

public class AddFriendCommand extends Command {
    private static final int FRIEND_NAME_TOKEN = 1;

    AddFriendCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        return ActionManager.getInstance().addFriend(args[FRIEND_NAME_TOKEN], sc);
    }
}
