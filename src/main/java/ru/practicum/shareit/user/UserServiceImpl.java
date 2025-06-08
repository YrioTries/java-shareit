package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class UserServiceImpl implements UserService {

    private final InMemoryUserStorage storage;

    @Autowired
    public UserServiceImpl(@Qualifier("InMemoryUserStorage") InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public ArrayList<User> get() {
        return storage.get();
    }

    public User create(User user) {
        if (storage.getUserIds().contains(user.getId()))
            throw new ConflictException("Такой пользователь уже существует");

        if (storage.get()
                .stream()
                .anyMatch(userStream -> userStream.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }

        if (user.getEmail() == null)
            throw new NotFoundException("Создание пользователя без почты");

        return storage.create(user);
    }

    public User update(User user) {
        if(!storage.getUserIds().contains(user.getId()))
            throw new NotFoundException("Невозможно обновить пользователя которого нет");
        return storage.update(user);
    }

    public void delete(long id) {
        storage.delete(id);
    }
}
