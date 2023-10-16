package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.ActionManager;

import java.nio.channels.SocketChannel;

public class GetHistoryCommand extends Command {
    GetHistoryCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        return ActionManager.getInstance().getPaymentsHistory(sc);
    }
}
