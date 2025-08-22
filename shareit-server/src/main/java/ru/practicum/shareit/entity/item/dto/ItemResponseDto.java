package ru.practicum.shareit.entity.item.dto;

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
