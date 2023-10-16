package bg.sofia.uni.fmi.mjt.splitwise.command;

import java.nio.channels.SocketChannel;

public class UnknownCommand extends Command {
    UnknownCommand(SocketChannel sc, String... args) {
        super(sc, args);
    }

    @Override
    public String execute() {
        // todo: const
        return "Unknown command";
    }
}
