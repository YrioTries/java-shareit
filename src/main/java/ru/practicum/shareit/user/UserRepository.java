package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User createUser(User user);
    User updateUser(User user);
    User getUserById(Long userId);
    List<User> getAllUsers();
    void deleteUser(Long userId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
