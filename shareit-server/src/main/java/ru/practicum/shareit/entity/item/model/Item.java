package ru.practicum.shareit.entity.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.entity.itemRequest.ItemRequest;
import ru.practicum.shareit.entity.user.model.User;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @Column(name = "available", nullable = false)
    @NotNull(message = "Статус доступности обязателен")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Column(name = "request_id")
    private Long requestId;
}