package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDto getItemById(Long id);

    List<ItemDto> getItemByUserId(Long id);

    List<ItemDto> searchText(String text);

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long itemId, Long userId, Map<String, Object> updates);
}
