package ru.practicum.entity.item_request;

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
