package ru.practicum.shareit.entity.item.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import ru.practicum.shareit.entity.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequest {
    private long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
