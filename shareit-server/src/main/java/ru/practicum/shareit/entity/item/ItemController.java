package ru.practicum.shareit.entity.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.entity.comment.model.CommentDto;
import ru.practicum.shareit.entity.item.dto.ItemDto;
import ru.practicum.shareit.entity.item.services.ItemServiceImpl;

import java.util.List;
import java.util.Map;

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
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItemDtoWithBookingsAndComments(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItemByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchText(@RequestParam("text") String text) {
        return service.searchText(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid ItemDto item) {
        return service.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid Map<String, Object> updates) {
        return service.update(itemId, userId, updates);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }

}
