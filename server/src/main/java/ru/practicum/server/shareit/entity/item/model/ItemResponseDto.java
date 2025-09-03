package ru.practicum.server.shareit.entity.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponseDto {
    private Long itemId;
    private String name;
    private Long ownerId;
}
