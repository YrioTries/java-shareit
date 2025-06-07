package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.HashMap;

public class InMemoryUserStorage {
    private final HashMap<Long, User> userMap;
    private long idCounter;

    public InMemoryUserStorage(HashMap<Long, User> userMap) {
        this.userMap = userMap;
        idCounter = 0;
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

        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());

        userMap.put(user.getId(), updatedUser);

        return updatedUser;
    }

    public void delete(long id) {
        userMap.remove(id);
    }
}
