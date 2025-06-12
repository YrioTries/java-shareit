package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final InMemoryUserStorage storage;

    @Autowired
    public UserServiceImpl(@Qualifier("InMemoryUserStorage") InMemoryUserStorage storage) {
        this.storage = storage;
    }

    @Override
    public ArrayList<User> get() {
        return storage.getUserList();
    }

    @Override
    public User create(User user) {
        if (storage.getUserList()
                .stream()
                .anyMatch(userStream -> userStream.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }

        if (storage.getUserIds().contains(user.getId()))
            throw new ConflictException("Такой пользователь уже существует");

        return storage.create(user);
    }

    @Override
    public User update(Long id, Map<String, Object> updates) {
        if (storage.getUserList()
                .stream()
                .anyMatch(userStream -> userStream.getEmail().equals(updates.get("email")))) {
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }

        if(!storage.getUserIds().contains(id))
            throw new NotFoundException("Невозможно обновить пользователя которого нет");
        return storage.update(id, updates);
    }

    @Override
    public void delete(long id) {
        if(!storage.getUserIds().contains(id))
            throw new NotFoundException("Невозможно удалить пользователя которого нет");
        storage.delete(id);
    }
}
