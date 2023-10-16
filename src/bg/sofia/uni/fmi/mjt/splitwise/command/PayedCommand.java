package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.ActionManager;

import java.nio.channels.SocketChannel;

public class PayedCommand extends Command {
    private static final int AMOUNT_TOKEN = 1;
    private static final int NAME_TOKEN = 2;
    PayedCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        return ActionManager.getInstance().payed(Double.parseDouble(args[AMOUNT_TOKEN]), args[NAME_TOKEN], sc);
    }
}
