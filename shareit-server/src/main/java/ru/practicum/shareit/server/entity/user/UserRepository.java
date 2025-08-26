package ru.practicum.shareit.server.entity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.entity.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
