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
    public Item getByItemId(@RequestParam long id) {
        return service.getItemById(id);
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<Item> getItemByUserId(@RequestParam long id) {
//        return service.getItemByUserId(id);
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@Valid Item item) {
        return service.create(item);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item update(@PathVariable Long itemId,
                       @RequestBody @Valid Map<String, Object> updates) {
        return service.update(itemId, updates);
    }
}
