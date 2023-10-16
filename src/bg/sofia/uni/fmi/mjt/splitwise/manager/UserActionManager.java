package bg.sofia.uni.fmi.mjt.splitwise.manager;

import bg.sofia.uni.fmi.mjt.splitwise.entity.UserProfile;
import bg.sofia.uni.fmi.mjt.splitwise.exception.InternalErrorException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NotLoggedInException;

import bg.sofia.uni.fmi.mjt.splitwise.exception.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.DuplicateUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.AlreadyLoggedException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UsernameNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.IncorrectPasswordException;

import bg.sofia.uni.fmi.mjt.splitwise.repository.UserRepo;
import bg.sofia.uni.fmi.mjt.splitwise.utility.PasswordHasher;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UserActionManager implements UserActionAPI {
    private static final int PASSWORD_MIN_LENGTH = 8;
    private Map<SocketChannel, String> logged;
    private static UserActionManager instance = new UserActionManager();

    private UserActionManager() {
        logged = new HashMap<>();
    }

    public static UserActionManager getInstance() {
        return instance;
    }

    @Override
    public String register(String username, String password, SocketChannel sc) {
        try {
            validateRegisterArguments(username, password, sc);
        } catch (InvalidUsernameException | DuplicateUsernameException | InvalidPasswordException |
                 AlreadyLoggedException | InternalErrorException e) {
            return e.getMessage();
        }

        String hashedPassword = null;
        try {
            hashedPassword = PasswordHasher.hashPassword(password);
        } catch (InternalErrorException e) {
            return MessageConstants.INTERNAL_ERROR_MSG;
        }

        // create new user
        UserRepo.getInstance().insert(username, new UserProfile(username, hashedPassword));

        // login
        logged.put(sc, username);

        return MessageConstants.REGISTER_SUCCESS_MSG;
    }

    @Override
    public String login(String username, String password, SocketChannel sc) {
        try {
            validateLoginArguments(username, password, sc);
        } catch (InvalidUsernameException | InvalidPasswordException |
                 AlreadyLoggedException | UsernameNotFoundException | IncorrectPasswordException e) {
            return e.getMessage();
        } catch (InternalErrorException e) {
            return MessageConstants.INTERNAL_ERROR_MSG;
        }

        // login
        logged.put(sc, username);
        UserRepo.getInstance().get(username).setLastLoginTime(LocalDateTime.now());

        return MessageConstants.LOGIN_SUCCESS_MSG + ActionManager.getInstance().getNotifications(sc);
    }

    @Override
    public String getUsername(SocketChannel sc) throws NotLoggedInException, InternalErrorException {
        if (sc == null) {
            throw new InternalErrorException(MessageConstants.INTERNAL_ERROR_MSG);
        }

        if (!logged.containsKey(sc)) {
            throw new NotLoggedInException(MessageConstants.NOT_LOGGED_ERROR_MSG);
        }

        return logged.get(sc);
    }

    @Override
    public String logout(SocketChannel sc) {
        if (sc == null) {
            throw new InternalErrorException(MessageConstants.INTERNAL_ERROR_MSG);
        }

        if (!logged.containsKey(sc)) {
            return MessageConstants.NOT_LOGGED_ERROR_MSG;
        }

        UserRepo.getInstance().get(logged.get(sc)).setLastLogoutTime(LocalDateTime.now());
        logged.remove(sc);

        return MessageConstants.LOGOUT_MSG;
    }

    private void validateRegisterArguments(String username, String password, SocketChannel sc)
        throws DuplicateUsernameException, InvalidUsernameException, InvalidPasswordException, AlreadyLoggedException,
        InternalErrorException {

        if (sc == null) {
            throw new InternalErrorException(MessageConstants.INTERNAL_ERROR_MSG);
        }

        if (logged.containsKey(sc)) {
            throw new AlreadyLoggedException(
                String.format(String.format(MessageConstants.ALREADY_LOGGED_ERROR_MSG, logged.get(sc))));
        }

        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException(MessageConstants.NULL_ARG_ERROR_MSG);
        }

        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException(MessageConstants.NULL_ARG_ERROR_MSG);
        }

        if (!username.matches(MessageConstants.USERNAME_PATTERN)) {
            throw new InvalidUsernameException(MessageConstants.INVALID_USERNAME_ERROR_MSG);
        }

        if (UserRepo.getInstance().contains(username)) {
            throw new DuplicateUsernameException("The entered username already exists in the database");
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new InvalidPasswordException(MessageConstants.WEAK_PASSWORD_MSG);
        }

        // weak password
        if (!password.matches(MessageConstants.PASSWORD_PATTERN)) {
            throw new InvalidPasswordException(MessageConstants.WEAK_PASSWORD_MSG);
        }

    }

    private void validateLoginArguments(String username, String password, SocketChannel sc)
        throws InvalidUsernameException, InvalidPasswordException, AlreadyLoggedException,
        UsernameNotFoundException, IncorrectPasswordException, InternalErrorException {

        if (sc == null) {
            throw new InternalErrorException(MessageConstants.INTERNAL_ERROR_MSG);
        }

        if (logged.containsKey(sc)) {
            throw new AlreadyLoggedException(
                String.format(MessageConstants.ALREADY_LOGGED_ERROR_MSG, logged.get(sc)));
        }

        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException(MessageConstants.NULL_ARG_ERROR_MSG);
        }

        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException(MessageConstants.NULL_ARG_ERROR_MSG);
        }

        // username does not exist
        if (!UserRepo.getInstance().contains(username)) {
            throw new UsernameNotFoundException(MessageConstants.USER_NOT_FOUND_ERROR_MSG + username);
        }

        // password and username don't match
        String hashedPassword = UserRepo.getInstance().get(username).password();
        if (!PasswordHasher.verifyPassword(password, hashedPassword)) {
            throw new IncorrectPasswordException(MessageConstants.WRONG_PASSWORD_ERROR_MSG);
        }

    }
}
