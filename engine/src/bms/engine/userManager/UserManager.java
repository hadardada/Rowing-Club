package bms.engine.userManager;

import bms.engine.Engine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private final Set<String> usersOnline;
    Engine bmsEngine;

    public UserManager(Engine bmsEngine) {

        usersOnline = new HashSet<>();
        this.bmsEngine = bmsEngine;
    }

    public void addUser(String username) {
        usersOnline.add(username);
    }

    public void removeUser(String username) {
        usersOnline.remove(username);
    }

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(usersOnline);
    }

    public boolean isUserExists(String username) {
        return usersOnline.contains(username);
    }

    public boolean isUserManager(String username) {return (bmsEngine.getMemberByEmail(username)).getIsManager();}
}
