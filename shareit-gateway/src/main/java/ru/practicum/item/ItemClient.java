package ru.practicum.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.entity.comment.model.CommentDto;
import ru.practicum.shareit.entity.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient {

    private final RestTemplate restTemplate;

    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                .build();
    }

    public ResponseEntity<ItemDto> getItemById(Long id, Long userId) {
        return restTemplate.getForEntity("/{id}?userId={userId}", ItemDto.class, id, userId);
    }

    public ResponseEntity<List<ItemDto>> getItemByUserId(Long userId) {
        return restTemplate.exchange("?userId={userId}", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ItemDto>>() {});
    }

    public ResponseEntity<List<ItemDto>> searchText(String text) {
        return restTemplate.exchange("/search?text={text}", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ItemDto>>() {}, text);
    }

    public ResponseEntity<ItemDto> create(Long userId, ItemDto itemDto) {
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto);
        return restTemplate.exchange("?userId={userId}", HttpMethod.POST, requestEntity, ItemDto.class, userId);
    }

    public ResponseEntity<ItemDto> update(Long itemId, Long userId, Map<String, Object> updates) {
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updates);
        return restTemplate.exchange("/{itemId}?userId={userId}", HttpMethod.PATCH, requestEntity, ItemDto.class, itemId, userId);
    }

    public ResponseEntity<CommentDto> addComment(Long itemId, Long userId, CommentDto commentDto) {
        HttpEntity<CommentDto> requestEntity = new HttpEntity<>(commentDto);
        return restTemplate.exchange("/{itemId}/comment?userId={userId}", HttpMethod.POST, requestEntity, CommentDto.class, itemId, userId);
    }
}
