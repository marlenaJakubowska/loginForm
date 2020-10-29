package com.codecool.loginPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDB {

    private final List<User> userList;
    private final Map<Integer, User> userSessionMap;


    public LocalDB() {
        this.userList = new ArrayList<>();
        this.userSessionMap = new HashMap<>();
        userList.add(new User("moira", "qwerty"));
        userList.add(new User("freja", "qwerty"));
        userList.add(new User("roman", "qwerty"));
        userList.add(new User("behemot", "qwerty"));
    }

    public User getUserBySessionId(int sessionId) {
        return userSessionMap.get(sessionId);
    }

    public User getUserByName(String userName) {
        return userList.stream().filter(u -> u.getUserName().equals(userName)).findFirst().orElse(null);
    }

    public Map<Integer, User> getSessionUserMap() {
        return userSessionMap;
    }

}
