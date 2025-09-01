package ru.practicum.entity.item_request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseDto> items;
}
