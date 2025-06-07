package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

public class UserServiceImpl implements UserService {

    InMemoryUserStorage storage;

    public UserServiceImpl(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        return storage.create(user);
    }

    public void delete(long id) {
        storage.delete(id);
    }
}
