package ru.practicum.server.shareit.entity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.shareit.entity.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
