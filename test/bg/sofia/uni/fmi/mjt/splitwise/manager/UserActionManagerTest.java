package bg.sofia.uni.fmi.mjt.splitwise.manager;

import bg.sofia.uni.fmi.mjt.splitwise.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class UserActionManagerTest {

    private static final String USERNAME = "user1";
    private static final String PASSWORD = "Mock_122";
    private final UserActionManager userMan = UserActionManager.getInstance();

    private final SocketChannel sc = mock(SocketChannel.class);

    // todo: test password stored in hashed format

    @AfterEach
    void after() {
        if (UserRepo.getInstance().contains(USERNAME)) {
            UserRepo.getInstance().delete(USERNAME);
        }
    }

    @Test
    void testRegisterWithWeakPassword() {
        String weakPassword = "weak1";
        assertEquals(MessageConstants.WEAK_PASSWORD_MSG, userMan.register(USERNAME, weakPassword, sc),
            "There should be a weak password check when registering");
    }

    @Test
    void testRegisterWhenAlreadyLogged() {
        userMan.register(USERNAME, PASSWORD, sc);
        assertEquals(String.format(MessageConstants.ALREADY_LOGGED_ERROR_MSG, USERNAME),
            userMan.login(USERNAME, PASSWORD, sc),
            "Registering when already logged in should not be allowed");
    }

    @Test
    void testRegisterWithInvalidUsername() {
        String invalid = "user";
        assertEquals(MessageConstants.INVALID_USERNAME_ERROR_MSG, userMan.register(invalid, PASSWORD, sc),
            "There should be an invalid username check when registering");
    }

    @Test
    void testRegisterWithInvalidParams() {
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.register(null, PASSWORD, sc),
            "Username was null when registering");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.register(USERNAME, null, sc),
            "Password was null when registering");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.register(USERNAME, " ", sc),
            "Password was empty when registering");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.register(" ", PASSWORD, sc),
            "Username was empty when registering");
    }

    @Test
    void testRegisterInternalServerError() {
        assertEquals(MessageConstants.INTERNAL_ERROR_MSG, userMan.register(USERNAME, PASSWORD, null),
            "There should be an internal server error message when socket channel is null");
    }

    @Test
    void testLogout() {
        userMan.register(USERNAME, PASSWORD, sc);
        assertEquals(MessageConstants.LOGOUT_MSG, userMan.logout(sc),
            "Logout should work correctly");
        assertTrue(userMan.login(USERNAME, PASSWORD, sc).contains(MessageConstants.LOGIN_SUCCESS_MSG),
            "Logout should work correctly");
    }

    @Test
    void testLoginUserNotFound() {
        assertEquals(String.format(MessageConstants.USER_NOT_FOUND_ERROR_MSG + USERNAME),
            userMan.login(USERNAME, PASSWORD, sc),
            "There should be a check if username does not exist when logging in");
    }

    @Test
    void testLoginAlreadyLogged() {
        userMan.register(USERNAME, PASSWORD, sc);
        assertEquals(String.format(String.format(MessageConstants.ALREADY_LOGGED_ERROR_MSG, USERNAME)),
            userMan.login(USERNAME, PASSWORD, sc), "Logging in when already logged in should not be allowed");
    }

    @Test
    void testLoginIncorrectPassword() {
        String otherPassword = "otherPass_12";
        userMan.register(USERNAME, PASSWORD, sc);
        userMan.logout(sc);
        assertEquals((MessageConstants.WRONG_PASSWORD_ERROR_MSG), userMan.login(USERNAME, otherPassword, sc),
            "There should be an wrong password check when logging in");
    }

    @Test
    void testLoginInvalidArguments() {
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.login(USERNAME, null, sc),
            "Password was null when registering");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.login(null, PASSWORD, sc),
            "Username was null when registering");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.login(USERNAME, "", sc),
            "Password was empty when registering");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.login("", PASSWORD, sc),
            "Username was empty when registering");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.login(USERNAME, " ", sc),
            "Password was blank when registering");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, userMan.login(" ", PASSWORD, sc),
            "Username was blank when registering");
    }

    @Test
    void testInternalServerError() {
        assertEquals(MessageConstants.INTERNAL_ERROR_MSG, userMan.login(USERNAME, PASSWORD, null),
            "There should be an internal server error message when socket channel is null");
    }

}
