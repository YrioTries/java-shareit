package ru.practicum.shareit.entity.user;

import ru.practicum.shareit.entity.user.model.User;
import ru.practicum.shareit.entity.user.model.dto.UserDto;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserDto> getUserList();

    UserDto getUserDto(Long id);

    User getUser(Long id);

    UserDto create(UserDto user);

    UserDto update(Long id, Map<String, Object> updates);

    void delete(long id);

    void validateUserExists(Long id) throws NotFoundException;
}
