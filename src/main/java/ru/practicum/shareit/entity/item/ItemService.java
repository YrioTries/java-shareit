package ru.practicum.shareit.entity.item;

import ru.practicum.shareit.entity.comment.model.CommentDto;
import ru.practicum.shareit.entity.item.model.Item;
import ru.practicum.shareit.entity.item.model.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDto getItemDtoById(Long id);

    Item getItemById(Long id);

    List<ItemDto> getItemByUserId(Long id);

    List<ItemDto> searchText(String text);

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long itemId, Long userId, Map<String, Object> updates);

    ItemDto getItemDtoWithBookingsAndComments(Long ownerId, Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
