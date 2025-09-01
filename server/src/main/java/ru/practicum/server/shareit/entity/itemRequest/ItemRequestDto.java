package ru.practicum.server.shareit.entity.itemRequest;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.server.shareit.entity.item.model.ItemResponseDto;

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
