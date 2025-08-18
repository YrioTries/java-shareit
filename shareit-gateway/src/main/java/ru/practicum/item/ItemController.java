package ru.practicum.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.entity.comment.model.CommentDto;
import ru.practicum.shareit.entity.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ItemDto>> getItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ItemDto>> searchText(@RequestParam("text") String text) {
        return itemClient.searchText(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody ItemDto item) {
        return itemClient.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody Map<String, Object> updates) {
        return itemClient.update(itemId, userId, updates);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
