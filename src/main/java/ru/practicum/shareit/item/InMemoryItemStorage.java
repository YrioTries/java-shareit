package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.model.User;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage {

    private final InMemoryUserStorage userStorage;

    private final HashMap<Long, Item> itemMap;

    private long idCounter;

    @Autowired
    public InMemoryItemStorage(HashMap<Long, Item> itemMap, InMemoryUserStorage userStorage) {
        this.itemMap = itemMap;
        this.userStorage = userStorage;
        idCounter = 1;
    }

    protected List<Long> getUsersIds() {
        return itemMap.values().stream()
                .map(item -> item.getOwner().getId())
                .toList();
    }

    protected ArrayList<Long> getItemsIds() {
        return new ArrayList<>(itemMap.keySet());
    }

    public Item getItemById(long id) {
        return itemMap.get(id);
    }

    public List<Item> getItemByUserId(long id) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().getId() == id)
                .toList();
    }

    public boolean isUserExist(long userId) {
        return userStorage.isUserExist(userId);
    }

    public Item create(Item item) {
        Item newItem = new Item(
                idCounter++,  // Генерация нового ID
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );

        itemMap.put(newItem.getId(), newItem);
        return newItem;  // Возвращаем объект с id
    }

    public Item update(long itemId, Map<String, Object> updates) {
        Item existingItem = itemMap.get(itemId);

        if (updates.containsKey("name")) {
            existingItem.setName((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            existingItem.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("available")) {
            existingItem.setAvailable((Boolean) updates.get("available"));
        }
        existingItem.setId(itemId);

        itemMap.put(existingItem.getId(), existingItem);
        return existingItem;
    }

}
