package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Map;

public interface UserService {

    ArrayList<UserDto> getUserList();

    UserDto getUser(Long id);

    UserDto create(UserDto user);

    UserDto update(Long id, Map<String, Object> updates);

    void delete(long id);
}
