package bg.sofia.uni.fmi.mjt.splitwise.manager;

import bg.sofia.uni.fmi.mjt.splitwise.exception.InternalErrorException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.splitwise.repository.UserRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class ActionManagerTest {
    private static final String NOT_LOGGED_NAME = "notLogged";
    private static final String LOGGED_NAME = "Logged";
    private static final String LOGGED_NAME2 = "Logged2";
    private static final String LOGGED_NAME3 = "Logged3";
    private static final String GROUP_NAME = "name";
    private ActionManager manager = ActionManager.getInstance();
    private final SocketChannel sc = mock(SocketChannel.class);
    private static SocketChannel logged = mock(SocketChannel.class);

    @BeforeAll
    static void registerFriend() {
        SocketChannel friend = mock(SocketChannel.class);
        UserActionManager.getInstance().register(LOGGED_NAME, "Secure_pass2", friend);
        SocketChannel friend2 = mock(SocketChannel.class);
        UserActionManager.getInstance().register(LOGGED_NAME2, "Secure_pass1", friend2);
        SocketChannel friend3 = mock(SocketChannel.class);
        UserActionManager.getInstance().register(LOGGED_NAME3, "Secure_pass3", friend3);
        UserActionManager.getInstance().register("username", "Str1_pass", logged);
    }

    @BeforeEach
    void registerUser() {
        UserActionManager.getInstance().login("username", "Str1_pass", logged);
    }

    @Test
    void testAddFriendWhenNotLoggedIn() {
        assertEquals(MessageConstants.NOT_LOGGED_ERROR_MSG, manager.addFriend(LOGGED_NAME, sc),
            "Not logged users cannot add friends");
    }

    @Test
    void testAddFriendInvalidParams() {
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, manager.addFriend(null, logged),
            "Friend name was null when adding a friend");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, manager.addFriend(" ", logged),
            "Friend name was blank when adding a friend");
    }

    @Test
    void testAddFriendNotFound() {
        assertEquals(MessageConstants.USER_NOT_FOUND_ERROR_MSG + NOT_LOGGED_NAME,
            manager.addFriend(NOT_LOGGED_NAME, logged),
            "There should be a check not to add friends that are not in the database");
    }

    @Test
    void testAddFriendSuccess() {
        assertEquals(String.format(MessageConstants.ADD_FRIEND_SUCCESS_MSG, LOGGED_NAME),
            manager.addFriend(LOGGED_NAME, logged), "Adding friends should work successfully!");
        assertEquals(MessageConstants.ALREADY_FRIEND_ERROR_MSG,
            manager.addFriend(LOGGED_NAME, logged), "Adding friends that already exist should be invalid!");
    }

    // String createGroup(String groupName, List<String> groupUsernames, SocketChannel sc);

    @Test
    void testCreateGroupWhenNotLoggedIn() {
        assertEquals(MessageConstants.NOT_LOGGED_ERROR_MSG,
            manager.createGroup(GROUP_NAME, List.of("user1", "user2"), sc), "Not logged users cannot create groups");
    }

    @Test
    void testCreateGroupInvalidParams() {
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG,
            manager.createGroup(null, List.of(LOGGED_NAME, LOGGED_NAME2), logged),
            "Null response expected when group name is null");

        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG,
            manager.createGroup(" ", List.of(LOGGED_NAME, LOGGED_NAME2), logged),
            "Null response expected when group name is blank");

        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG,
            manager.createGroup(GROUP_NAME, List.of(" ", LOGGED_NAME), logged),
            "Null response expected when a username is blank");

        List<String> namesList = new ArrayList<>();
        namesList.add(LOGGED_NAME);
        namesList.add(null);

        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG,
            manager.createGroup(GROUP_NAME, namesList, logged),
            "Null response expected when a username is null");
    }

    @Test
    void testCreateGroupInvalidGroupNames() {
        assertEquals(MessageConstants.INVALID_GROUP_NAME_ERROR_MSG,
            manager.createGroup("#########", List.of(LOGGED_NAME, LOGGED_NAME2), logged),
            "Creating group with invalid name should not happen");
    }

    @Test
    void testCreateGroupUserNotFound() {
        assertEquals(MessageConstants.USER_NOT_FOUND_ERROR_MSG + "user2",
            manager.createGroup(GROUP_NAME, List.of(LOGGED_NAME, "user2"), logged),
            "Creating groups with names that are not in the database should not be possible");
    }

    @Test
    void testCreateGroupSuccess() {
        assertEquals(MessageConstants.NEW_GROUP_SUCCESS_MSG,
            manager.createGroup("name2", List.of(LOGGED_NAME, LOGGED_NAME2), logged),
            "Creating a group should work correctly");
    }

    @Test
    void testCreateGroupDuplicateNames() {
        assertEquals(MessageConstants.NEW_GROUP_SUCCESS_MSG,
            manager.createGroup("name3", List.of(LOGGED_NAME, LOGGED_NAME2), logged),
            "Creating a group should work correctly");

        assertEquals(MessageConstants.DUPLICATE_GROUP_NAME_ERROR_MSG,
            manager.createGroup("name3", List.of(LOGGED_NAME, LOGGED_NAME2), logged),
            "User creating a group with the same name should be invalid");
    }

    // String splitGroup(double amount, String groupName, String reason, SocketChannel sc);

    @Test
    void testSplitGroupWhenNotLoggedIn() {
        assertEquals(MessageConstants.NOT_LOGGED_ERROR_MSG, manager.splitGroup(1, GROUP_NAME, "REASON", sc),
            "Not logged users cannot split payments");
    }

    @Test
    void testSplitGroupInvalidParams() {
        assertEquals(MessageConstants.NEW_GROUP_SUCCESS_MSG,
            manager.createGroup(GROUP_NAME, List.of(LOGGED_NAME, "username"), logged),
            "Creating a group should work correctly");

        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, manager.splitGroup(1, GROUP_NAME, null, logged),
            "Reason should not be null when splitting");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, manager.splitGroup(1, GROUP_NAME, " ", logged),
            "Reason should not be blank when splitting");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, manager.splitGroup(1, null, "REASON", logged),
            "groupName should not be null when splitting");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, manager.splitGroup(1, " ", "REASON", logged),
            "groupName should not be blank when splitting");
        assertEquals(MessageConstants.INVALID_SUM_ERROR_MSG, manager.splitGroup(-1, GROUP_NAME, "REASON", logged),
            "Amount should be more than 0.0 when splitting");
    }

    @Test
    void testSplitGroupNotFound() {
        assertEquals(MessageConstants.GROUP_NOT_FOUND_ERROR_MSG, manager.splitGroup(1, "name4", "REASON", logged),
            "Splitting payment in a group that does not exist should work properly");
    }

    @Test
    void testSplitGroupSuccess() throws InternalErrorException, NotLoggedInException {
        assertEquals(MessageConstants.NEW_GROUP_SUCCESS_MSG,
            manager.createGroup("name5", List.of(LOGGED_NAME, "username"), logged),
            "Creating a group should work correctly");

        assertTrue(manager.splitGroup(18, "name5", "REASON", logged)
                .contains(String.format(MessageConstants.SPLIT_SUCCESS_MSG, 18.0, "name5")),
            "Splitting should work correctly");
        assertEquals(9.0,
            UserRepo.getInstance().get(UserActionManager.getInstance().getUsername(logged)).getBalanceWith(LOGGED_NAME),
            "Calculating balance when splitting should work properly");
    }

    // String payed(double amount, String friendUsername, SocketChannel sc);

    @Test
    void testPayedWhenNotLoggedIn() {
        assertEquals(MessageConstants.NOT_LOGGED_ERROR_MSG, manager.payed(1, GROUP_NAME, sc),
            "Not logged users cannot approve payments");
    }

    @Test
    void testPayedInvalidParams() {
        assertEquals(String.format(MessageConstants.ADD_FRIEND_SUCCESS_MSG, LOGGED_NAME3),
            manager.addFriend(LOGGED_NAME3, logged),
            "Adding a friend should work correctly");

        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, manager.payed(10, null, logged),
            "Name should not be null when approving payment");
        assertEquals(MessageConstants.NULL_ARG_ERROR_MSG, manager.payed(1, " ", logged),
            "Name should not be blank when approving payment");
        assertEquals(MessageConstants.INVALID_SUM_ERROR_MSG, manager.payed(-1, LOGGED_NAME3, logged),
            "Amount should be more than 0.0 when approving payment");
    }

    @Test
    void testPayedUserNotFound() {
        // TODO: IMPLEMENT APPROVING PAYMENT FROM GROUP MEMBERS THAT ARE NOT FRIENDS
        assertEquals(MessageConstants.GROUP_NOT_FOUND_ERROR_MSG, manager.payed(18, "someName", logged),
            "Approving payment from non friends should not work");
    }

    @Test
    void testPayedSuccess() throws InternalErrorException, NotLoggedInException {
        assertEquals(String.format(MessageConstants.ADD_FRIEND_SUCCESS_MSG, LOGGED_NAME2),
            manager.addFriend(LOGGED_NAME2, logged),
            "Adding a friend should work correctly");

        assertTrue(manager.payed(18, LOGGED_NAME2, logged)
                .contains(String.format(MessageConstants.PAYED_SUCCESS_MSG, LOGGED_NAME2, 18.0)),
            "Splitting should work correctly");

        assertEquals(-18.0,
            UserRepo.getInstance().get(UserActionManager.getInstance().getUsername(logged))
                .getBalanceWith(LOGGED_NAME2),
            "Calculating balance when splitting should work properly");
    }


    // String getStatus(SocketChannel sc);
    @Test
    void testGetStatusWhenNotLoggedIn() {
        assertEquals(MessageConstants.NOT_LOGGED_ERROR_MSG, manager.getStatus(sc),
            "Not logged users cannot see status");
    }

    // String seeAll(SocketChannel sc);
    @Test
    void testSeeAllWhenNotLoggedIn() {
        assertEquals(MessageConstants.NOT_LOGGED_ERROR_MSG, manager.seeAll(sc),
            "Not logged users cannot see friends and groups");
    }

    // String getPaymentsHistory(SocketChannel sc);
    @Test
    void testGetPaymentsHistoryWhenNotLoggedIn() {
        assertEquals(MessageConstants.NOT_LOGGED_ERROR_MSG, manager.getPaymentsHistory(sc),
            "Not logged users cannot see payments history");
    }


}