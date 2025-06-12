package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {

    Item getItemById(long id);

    List<Item> getItemByUserId(long id);

    Item create(Long userId, Item item);

    Item update(Long itemId, Long userId, Map<String, Object> updates);
}
