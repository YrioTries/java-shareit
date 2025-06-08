package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService service;

    public UserController(@Qualifier("InMemoryService") UserService service) {
        this.service = service;
    }

    @PostMapping
    public User create(User user) {
        return service.create(user);
    }

    @PatchMapping
    public User update(User user) {
        return service.update(user);
    }

    @DeleteMapping
    public void delete(long id) {
        service.delete(id);
    }

}
