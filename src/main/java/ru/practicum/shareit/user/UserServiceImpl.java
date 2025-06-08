package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

@Qualifier("InMemoryService")
public class UserServiceImpl implements UserService {

    InMemoryUserStorage storage;

    public UserServiceImpl(@Qualifier("InMemoryUserStorage") InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public ArrayList<User> get() {
        return storage.get();
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
