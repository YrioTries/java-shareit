package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Entity
@AllArgsConstructor
@Getter @Setter @ToString
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Column(name = "description")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @Column(name = "available", nullable = false)
    @NotNull(message = "Статус доступности обязателен")
    private Boolean available;

    @Column(name = "owner")
    private User owner;

    @Column(name = "request")
    private ItemRequest request;
}
