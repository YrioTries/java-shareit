package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public boolean isItemExist(Long id) {
        return itemMap.containsKey(id);
    }

    public Item getItemById(Long id) {
        return itemMap.get(id);
    }

    public List<ItemDto> getItemsByUserId(Long id) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().getId().equals(id))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public List<ItemDto> searchText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        String searchText = text.trim().toUpperCase();

        return itemMap.values()
                .stream()
                .filter(item -> {
                    String itemName = item.getName() != null ? item.getName().trim().toUpperCase() : "";
                    return itemName.contains(searchText) && item.getAvailable();
                })
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public boolean isUserExist(Long userId) {
        return userStorage.isUserExist(userId);
    }

    public ItemDto create(Long ownerId, ItemDto item) {
        Item newItem = new Item(
                idCounter++,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userStorage.getUser(ownerId),
                null
        );

        itemMap.put(newItem.getId(), newItem);
        return ItemMapper.toItemDto(newItem);
    }

    public ItemDto update(long itemId, Long userId, Map<String, Object> updates) {
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
        existingItem.setOwner(userStorage.getUser(userId));

        itemMap.put(existingItem.getId(), existingItem);
        return ItemMapper.toItemDto(existingItem);
    }
}
