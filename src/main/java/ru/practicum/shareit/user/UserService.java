package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserDto> getUserList();

    UserDto getUser(Long id);

    UserDto create(UserDto user);

    UserDto update(Long id, Map<String, Object> updates);

    void delete(long id);
}
