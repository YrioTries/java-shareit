package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;

@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage {
    private final HashMap<Long, User> userMap;
    private long idCounter;

    public InMemoryUserStorage(HashMap<Long, User> userMap) {
        this.userMap = userMap;
        idCounter = 0;
    }

    protected ArrayList<Long> getUserIds() {
        return new ArrayList<Long>(userMap.keySet());
    }

    public ArrayList<User> get() {
        return new ArrayList<User>(userMap.values());
    }

    public User create (User user) {
        User newUser = new User(
                idCounter++,
                user.getName(),
                user.getEmail()
        );

        userMap.put(newUser.getId(), user);

        return newUser;
    }

    public User update(User user) {
        User updatedUser = userMap.get(user.getId());
        if (user.getName() != null)
            updatedUser.setName(user.getName());
        if(user.getEmail() != null)
            updatedUser.setEmail(user.getEmail());

        userMap.put(user.getId(), updatedUser);

        return updatedUser;
    }

    public void delete(long id) {
        userMap.remove(id);
    }
}
