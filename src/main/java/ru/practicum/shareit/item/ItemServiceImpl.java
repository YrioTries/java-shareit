package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
    public Item getItemById(long id) {
        return storage.getItemById(id);
    }

    @Override
    public List<Item> getItemByUserId(long id) {
        return storage.getItemByUserId(id);
    }

    @Override
    public Item create(Long userId, Item item) {
        if (userId == null)
            throw new NotFoundException("Не указан владелец предмета при создании");

        if (!storage.isUserExist(userId))
            throw new NotFoundException("Владелец не найден");

        return storage.create(item);
    }

    @Override
    public Item update(Long itemId, Long userId, Map<String, Object> updates) {

        Item existingItem = storage.getItemById(itemId);

        if (existingItem == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }

        if (userId == null)
            throw new NotFoundException("Не указан владелец предмета при создании");

        if (!storage.isUserExist(userId))
            throw new NotFoundException("Владелец не найден");

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only item owner can update it");
        }

        return storage.update(itemId, userId, updates);
    }
}
