package ru.practicum.shareit.entity.itemRequest;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.entity.item.model.ItemResponseDto;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "requester", ignore = true)
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    default ItemRequestDto toItemRequestDtoWithItems(ItemRequest itemRequest, List<ItemResponseDto> items) {
        ItemRequestDto dto = toItemRequestDto(itemRequest);
        dto.setItems(items);
        return dto;
    }
}
