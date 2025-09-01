package ru.practicum.shareit.entity.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.entity.booking.BookingRepository;
import ru.practicum.shareit.entity.comment.CommentRepository;
import ru.practicum.shareit.entity.user.model.User;
import ru.practicum.shareit.entity.user.model.dto.UserMapper;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.entity.user.model.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUserList() {
        log.info("Получение списка всех пользователей");
        List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Получено {} пользователей", users.size());
        return users;
    }

    @Override
    public UserDto getUserDto(Long id) {
        log.info("Получение пользователя с id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
        UserDto userDto = userMapper.toUserDto(user);
        log.info("Получен пользователь: {}", userDto);
        return userDto;
    }

    @Override
    public User getUser(Long id) {
        log.info("Получение сущности пользователя с id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
        log.info("Получена сущность пользователя: {}", user);
        return user;
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("Создание пользователя: {}", userDto);
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Пользователь с такой почтой уже существует");
        }
        User user = userMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        UserDto savedUserDto = userMapper.toUserDto(savedUser);
        log.info("Создан пользователь: {}", savedUserDto);
        return savedUserDto;
    }

    @Override
    @Transactional
    public UserDto update(Long id, Map<String, Object> updates) {
        log.info("Обновление пользователя с id={}, данные для обновления: {}", id, updates);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Невозможно обновить пользователя которого нет"));

        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            log.info("Обновление email для пользователя с id={} на {}", id, newEmail);
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmailAndIdNot(newEmail, id)) {
                throw new ConflictException("Пользователь с такой почтой уже существует");
            }
            user.setEmail(newEmail);
        }
        if (updates.containsKey("name")) {
            String newName = (String) updates.get("name");
            log.info("Обновление имени для пользователя с id={} на {}", id, newName);
            user.setName(newName);
        }

        User updatedUser = userRepository.save(user);
        UserDto updatedUserDto = userMapper.toUserDto(updatedUser);
        log.info("Обновлённый пользователь: {}", updatedUserDto);
        return updatedUserDto;
    }

    @Override
    @Transactional
    public void delete(long id) {
        log.info("Удаление пользователя с id={}", id);
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Невозможно удалить пользователя которого нет");
        }
        bookingRepository.deleteByBookerId(id);
        commentRepository.deleteByAuthorId(id);
        userRepository.deleteById(id);

        log.info("Пользователь с id={} удалён", id);
    }

    @Override
    public void validateUserExists(Long id) throws NotFoundException {
        log.info("Проверка существования пользователя с id={}", id);
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        log.info("Пользователь с id={} существует", id);
    }
}
