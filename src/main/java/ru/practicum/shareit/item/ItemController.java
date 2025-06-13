package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceImpl service;

    @Autowired
    public ItemController(ItemServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Item getByItemId(@PathVariable Long id) {
        return service.getItemById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItemByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<String> searchText(@RequestParam("text") String text) {
        return service.searchText(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid Item item){
        return service.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item update(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid Map<String, Object> updates) {
        return service.update(itemId, userId, updates);
    }
}
