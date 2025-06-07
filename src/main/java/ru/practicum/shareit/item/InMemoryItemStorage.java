package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryItemStorage {
    private final HashMap<Long, Item> itemMap;
    private long idCounter;

    public InMemoryItemStorage(HashMap<Long, Item> itemMap) {
        this.itemMap = itemMap;
        idCounter = 0;
    }

    protected ArrayList<Long> getItemsIds() {
        return new ArrayList<>(itemMap.keySet());
    }

    public Item create(Item item) {
        Item newItem = new Item(
                idCounter++,
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner(),
                item.getRequest()
        );

        itemMap.put(newItem.getId(), newItem);

        return newItem;
    }

    public Item update(Item item) {
        Item updatedItem = itemMap.get(item.getId());

        updatedItem.setId(item.getId());
        updatedItem.setName(item.getName());
        updatedItem.setDescription(item.getDescription());
        updatedItem.setAvailable(item.isAvailable());
        updatedItem.setOwner(item.getOwner());
        updatedItem.setRequest(item.getRequest());

        itemMap.put(item.getId(), updatedItem);

        return updatedItem;
    }

}
