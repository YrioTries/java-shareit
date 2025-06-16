package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemStorage storage;

    @Autowired
    public ItemServiceImpl(InMemoryItemStorage storage) {
        this.storage = storage;
    }

    @Override
    public ItemDto getItemById(Long id) {
        if (!storage.isItemExist(id))
            throw new NotFoundException("Предмета с таким id нет");

        return ItemMapper.toItemDto(storage.getItemById(id));
    }

    @Override
    public List<ItemDto> getItemByUserId(Long userId) {

        if (!storage.isUserExist(userId))
            throw new NotFoundException("Владелец не найден");

        return storage.getItemsByUserId(userId);
    }

    @Override
    public List<ItemDto> searchText(String text) {
        return storage.searchText(text);
    }

    @Override
    public ItemDto create(Long ownerId, Item item) {
        if (ownerId == null)
            throw new NotFoundException("Не указан владелец предмета при создании");

        if (!storage.isUserExist(ownerId))
            throw new NotFoundException("Владелец не найден");

        return storage.create(ownerId, item);
    }

    @Override
    public ItemDto update(Long itemId, Long userId, Map<String, Object> updates) {

        Item existingItem = storage.getItemById(itemId);

        if (existingItem == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }

        if (userId == null)
            throw new NotFoundException("Не указан владелец предмета при создании");


        if (!storage.isUserExist(userId))
            throw new NotFoundException("Владелец не найден");

        return storage.update(itemId, userId, updates);
    }
}
