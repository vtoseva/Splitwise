package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.ActionManager;

import java.nio.channels.SocketChannel;

public class SplitCommand extends Command {
    private static final int AMOUNT_TOKEN = 1;
    private static final int GROUP_NAME_TOKEN = 2;
    private static final int REASON_TOKEN = 3;

    SplitCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        return ActionManager.getInstance()
            .splitGroup(Double.parseDouble(args[AMOUNT_TOKEN]), args[GROUP_NAME_TOKEN], args[REASON_TOKEN], sc);
    }
}
