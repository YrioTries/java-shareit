package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Map;

public interface UserService {

    ArrayList<User> getUserList();

    User getUser(Long id);

    User create(User user);

    User update(Long id, Map<String, Object> updates);

    void delete(long id);
}
