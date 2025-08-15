package ru.practicum.shareit.entity.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.entity.user.model.User;
import ru.practicum.shareit.entity.user.model.dto.UserMapperAn;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.entity.user.model.dto.UserDto;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapperAn userMapperAn;

    @Override
    public List<UserDto> getUserList() {
        return userRepository.findAll().stream()
                .map(userMapperAn::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDto(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
        return userMapperAn.toUserDto(user);
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }

        User user = userMapperAn.toUser(userDto);
        User savedUser = userRepository.save(user);
        return userMapperAn.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto update(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Невозможно обновить пользователя которого нет"));

        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmailAndIdNot(newEmail, id)) {
                throw new ConflictException("Пользователь с такой почтой уже существует");
            }
            user.setEmail(newEmail);
        }

        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }

        User updatedUser = userRepository.save(user);
        return userMapperAn.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Невозможно удалить пользователя которого нет");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void validateUserExists(Long id) throws NotFoundException {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}