package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Map;

public interface UserService {

    ArrayList<User> get();

    User create(User user);

    User update(Long id, Map<String, Object> updates);

    void delete(long id);
}
