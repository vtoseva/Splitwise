package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.ActionManager;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CreateGroupCommand extends Command {
    private static final int GROUP_NAME_TOKEN = 1;
    private static final int ARGS_TO_SKIP = 2;

    CreateGroupCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        return ActionManager.getInstance()
            .createGroup(args[GROUP_NAME_TOKEN], Arrays.stream(args).skip(ARGS_TO_SKIP).collect(Collectors.toList()),
                sc);
    }
}
