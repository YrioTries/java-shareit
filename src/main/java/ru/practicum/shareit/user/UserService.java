package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

public interface UserService {

    User create(User user);

    User update(User user);

    void delete(long id);
}
