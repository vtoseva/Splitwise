package bg.sofia.uni.fmi.mjt.splitwise.entity;

import java.util.Set;

public class Group {
    // friendships are groups with names ###
    private String name;
    private Set<String> usernames;

    public Group(String name, Set<String> usernames) {
        this.name = name;
        this.usernames = usernames;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> usernames() {
        return usernames;
    }

    public void setUsernames(Set<String> usernames) {
        this.usernames = usernames;
    }
}
