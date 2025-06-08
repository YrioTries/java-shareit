package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public interface UserService {

    ArrayList<User> get();

    User create(User user);

    User update(User user);

    void delete(long id);
}
