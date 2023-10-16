package bg.sofia.uni.fmi.mjt.splitwise.manager;

import bg.sofia.uni.fmi.mjt.splitwise.exception.InternalErrorException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NotLoggedInException;

import java.nio.channels.SocketChannel;

public interface UserActionAPI {

    String register(String username, String password, SocketChannel sc);

    String login(String username, String password, SocketChannel sc);

    String getUsername(SocketChannel sc) throws NotLoggedInException, InternalErrorException;

    String logout(SocketChannel sc);
}
