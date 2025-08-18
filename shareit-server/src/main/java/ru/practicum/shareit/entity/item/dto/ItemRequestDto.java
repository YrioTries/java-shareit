package ru.practicum.shareit.entity.item.dto;

import java.time.LocalDateTime;

public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseDto> items;
}
