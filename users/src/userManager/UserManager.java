package userManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserManager {
    private final Map<String, Boolean> usersOnline;

    public UserManager() {
        usersOnline = new HashMap<>();
    }

    public void addUser(String username, Boolean isManager) {
        usersOnline.put(username, isManager);
    }

    public void removeUser(String username) {
        usersOnline.remove(username);
    }

    public Map<String, Boolean> getUsers() {
        return Collections.unmodifiableMap(usersOnline);
    }

    public Set<String> getUserNames() {return Collections.unmodifiableSet(usersOnline.keySet())}

    public boolean isUserExists(String username) {
        return usersOnline.contains(username);
    }

    public boolean isUserManager(String username) {return usersOnline.get(username).booleanValue()}
}
