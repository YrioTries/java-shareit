package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage {
    private final HashMap<Long, User> userMap;
    private long idCounter;

    public InMemoryUserStorage(HashMap<Long, User> userMap) {
        this.userMap = userMap;
        idCounter = 1;
    }

    protected ArrayList<Long> getUserIds() {
        return new ArrayList<Long>(userMap.keySet());
    }

    public ArrayList<UserDto> getUserList() {
        return new ArrayList<UserDto>(
                userMap.values().stream()
                .map(UserMapper::toUserDto)
                .toList());
    }

    public boolean isUserExist(long id) {
        return userMap.containsKey(id);
    }

    public User getUser(Long id) {
        return userMap.get(id);
    }

    public UserDto create(UserDto user) {
        User newUser = new User(
                idCounter++,
                user.getName(),
                user.getEmail()
        );

        userMap.put(newUser.getId(), newUser);

        return UserMapper.toUserDto(newUser);
    }

    public UserDto update(Long id, Map<String, Object> updates) {
        User updatedUser = userMap.get(id);
        if (updates.containsKey("name"))
            updatedUser.setName((String) updates.get("name"));
        if (updates.containsKey("email"))
            updatedUser.setEmail((String) updates.get("email"));

        updatedUser.setId(id);

        userMap.put(updatedUser.getId(), updatedUser);

        return UserMapper.toUserDto(updatedUser);
    }

    public void delete(long id) {
        userMap.remove(id);
    }
}
