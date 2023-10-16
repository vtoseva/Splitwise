package bg.sofia.uni.fmi.mjt.splitwise.command;

import java.nio.channels.SocketChannel;

public abstract class Command {
    protected String[] args;
    protected SocketChannel sc;

    Command(SocketChannel sc, String... args) {
        this.args = args;
        this.sc = sc;
    }
    public abstract String execute();
}
