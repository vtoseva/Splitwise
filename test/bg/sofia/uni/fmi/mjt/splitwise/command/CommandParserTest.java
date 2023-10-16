package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.manager.MessageConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class CommandParserTest {
    private static final SocketChannel sc = mock(SocketChannel.class);
    private static final SocketChannel sc2 = mock(SocketChannel.class);
    private static final String USERNAME = "UserA";
    private static final String USER2 = "User2";
    private static final String PASSWORD = "Secure_123";

    @BeforeAll
    static void registerUser() {
        Command register = new RegisterCommand(sc, "register", USERNAME, PASSWORD);
        assertEquals(MessageConstants.REGISTER_SUCCESS_MSG, register.execute(),
            "Registration should be successful.");

        Command register2 = new RegisterCommand(sc2, "register", USER2, "Secure_123");
        assertEquals(MessageConstants.REGISTER_SUCCESS_MSG, register2.execute(),
            "Registration should be successful.");
    }

    @BeforeEach
    void loginUser() {
        Command login = new LoginCommand(sc, "login", USERNAME, PASSWORD);
        login.execute();

        Command login2 = new LoginCommand(sc2, "login", USER2, "Secure_123");
        login2.execute();
    }

    @Test
    void testLoginCommand() {
        Command login = new LoginCommand(sc, "login", USERNAME, PASSWORD);
        assertTrue(login.execute().contains(String.format(MessageConstants.ALREADY_LOGGED_ERROR_MSG, "UserA")),
            "Login command should work.");
    }

    @Test
    void testSplitCommand() {
        Command createGroup = new AddFriendCommand(sc, "add-friend", USER2);
        createGroup.execute();

        Command split = new SplitCommand(sc, "split", "10", USER2, "reason");
        assertTrue(split.execute().contains(String.format(MessageConstants.SPLIT_SUCCESS_MSG, 10.0, USER2)),
            "Splitting command should work properly.");

        Command payed = new PayedCommand(sc2, "payed", "5", USERNAME);
        assertTrue(payed.execute().contains(String.format(MessageConstants.PAYED_SUCCESS_MSG, USERNAME, "5.0"))
            , "Approving payment should be successful.");
    }

    @Test
    void testPayedCommand() {
        Command createGroup = new AddFriendCommand(sc, "add-friend", USER2);
        createGroup.execute();

        Command payed = new PayedCommand(sc, "payed", "10", USER2);

        assertTrue(payed.execute().contains(String.format(MessageConstants.PAYED_SUCCESS_MSG, USER2, "10.0"))
            , "Approving payment should be successful.");


        Command payed2 = new PayedCommand(sc2, "payed", "10", USERNAME);

        assertTrue(payed2.execute().contains(String.format(MessageConstants.PAYED_SUCCESS_MSG, USERNAME, "10.0"))
            , "Approving payment should be successful.");

    }

    @Test
    void testGetStatusCommand() {
        Command status = new GetStatusCommand(sc, "get-status");
        assertEquals(MessageConstants.NOTHING_TO_SHOW_MSG, status.execute(),
            "Get-status command should work properly.");

    }

    @Test
    void testLogoutCommand() {
        Command logout = new LogoutCommand(sc, "logout");
        assertEquals(MessageConstants.LOGOUT_MSG, logout.execute(),
            "Logout command should work properly.");

    }

    @Test
    void testSeeAllCommand() {
        Command createGroup = new AddFriendCommand(sc, "add-friend", USER2);
        createGroup.execute();

        Command see = new SeeAllCommand(sc, "see-all");
        assertTrue(see.execute().contains(USER2), "see-all command should work properly.");
    }

    @Test
    void testUnknownCommand() {
        Command unknown = new UnknownCommand(sc, "logout");
        assertEquals("Unknown command", unknown.execute(),
            "Unknown command should work properly.");

    }

    @Test
    void testHelpCommand() {
        Command help = new HelpCommand(sc, "help");
        assertEquals(MessageConstants.HELP, help.execute(),
            "Help command should work properly.");

    }


}
