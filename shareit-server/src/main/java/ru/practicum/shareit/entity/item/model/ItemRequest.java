package ru.practicum.shareit.entity.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.entity.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
