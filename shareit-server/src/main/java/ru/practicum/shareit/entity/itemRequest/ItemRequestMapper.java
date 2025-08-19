package ru.practicum.shareit.entity.itemRequest;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.entity.item.dto.ItemResponseDto;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    default ItemRequestDto toItemRequestDtoWithItems(ItemRequest itemRequest, List<ItemResponseDto> items) {
        ItemRequestDto dto = toItemRequestDto(itemRequest);
        dto.setItems(items);
        return dto;
    }
}
