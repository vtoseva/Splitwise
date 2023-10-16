package bg.sofia.uni.fmi.mjt.splitwise.entity;

import bg.sofia.uni.fmi.mjt.splitwise.exception.DuplicateGroupNameException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.GroupNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.manager.MessageConstants;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class UserProfile {
    private static final String FRIENDSHIP_DEF_NAME = "###";
    String username;
    String password;
    Map<String, Group> relations;
    // username, amount owesTo < 0, isOwed > 0
    Map<String, Double> balance;
    LocalDateTime lastLoginTime;
    LocalDateTime lastLogoutTime;

    public UserProfile(String username, String password) {
        this.username = username;
        this.password = password;
        relations = new HashMap<>();
        balance = new HashMap<>();
    }

    public void addGroup(Group group) throws DuplicateGroupNameException {
        if (relations.containsKey(group.name())) {
            throw new DuplicateGroupNameException(MessageConstants.DUPLICATE_GROUP_NAME_ERROR_MSG);
        }

        if (group.name().equals(FRIENDSHIP_DEF_NAME)) {
            String friendUsername = null;
            for (String name : group.usernames()) {
                if (!name.equals(this.username)) {
                    friendUsername = name;
                }
            }

            if (relations.containsKey(friendUsername)) {
                throw new DuplicateGroupNameException("Friend with this name already exists");
            }

            relations.put(friendUsername, group);
        } else {
            relations.put(group.name(), group);
        }
    }

    // implement removeGroup

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Group> relations() {
        return relations;
    }

    public void setRelations(Map<String, Group> relations) {
        this.relations = relations;
    }

    public Group getGroup(String name) throws GroupNotFoundException {
        if (!relations.containsKey(name)) {
            throw new GroupNotFoundException(MessageConstants.GROUP_NOT_FOUND_ERROR_MSG);
        }

        return relations.get(name);
    }

    public Map<String, Double> balance() {
        return balance;
    }

    public void setBalance(Map<String, Double> balance) {
        this.balance = balance;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public LocalDateTime getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(LocalDateTime lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }
    public double getBalanceWith(String otherUsername) {
        if (otherUsername == null || otherUsername.isBlank()) {
            throw new IllegalArgumentException(MessageConstants.NULL_ARG_ERROR_MSG);
        }

        return balance.getOrDefault(otherUsername, 0.0);
    }

    // if splitCount is equal to 1 than the action is get-payed, not split
    public void getPayed(String otherUsername, double amount, int splitCount) {
        balance.put(otherUsername, balance.getOrDefault(otherUsername, 0.0) - amount / splitCount);

        if (balance.containsKey(otherUsername) && balance.get(otherUsername) == 0.0) {
            balance.remove(otherUsername);
        }
    }

    // if splitCount is equal to 1 than the action is pay, not split
    // the list includes this User's username
    public void pay(Set<String> usernames, double amount, int splitCount) {
        for (String name : usernames) {
            if (!name.equals(this.username)) {
                balance.put(name, balance.getOrDefault(name, 0.0) + amount / splitCount);
                if (balance.containsKey(name) && balance.get(name) == 0.0) {
                    balance.remove(name);
                }
            }
        }
    }

}
