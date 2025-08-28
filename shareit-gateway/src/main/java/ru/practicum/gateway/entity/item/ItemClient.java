package ru.practicum.gateway.entity.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ItemClient {
    private final RestTemplate restTemplate;

    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        log.info("Инициализация ItemClient с URL сервера: {}", serverUrl);
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                .build();
    }

    @Cacheable(value = "items", key = "#id")
    public ResponseEntity<ItemDto> getItemById(Long id, Long userId) {
        log.info("Отправка запроса на получение информации о вещи с ID={} для пользователя с ID={}", id, userId);
        ResponseEntity<ItemDto> response = restTemplate.getForEntity("/{id}?userId={userId}", ItemDto.class, id, userId);
        log.info("Получена информация о вещи с ID={}: {}", id, response.getBody());
        return response;
    }

    @Cacheable(value = "items", key = "'user_' + #userId")
    public ResponseEntity<List<ItemDto>> getItemByUserId(Long userId) {
        log.info("Отправка запроса на получение списка вещей пользователя с ID={}", userId);
        ResponseEntity<List<ItemDto>> response = restTemplate.exchange(
                "?userId={userId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ItemDto>>() {},
                userId
        );
        log.info("Получено {} вещей для пользователя с ID={}", response.getBody() != null ? response.getBody().size() : 0, userId);
        return response;
    }

    @Cacheable(value = "items", key = "'search_' + #text")
    public ResponseEntity<List<ItemDto>> searchText(String text) {
        log.info("Отправка запроса на поиск вещей по тексту: {}", text);
        ResponseEntity<List<ItemDto>> response = restTemplate.exchange(
                "/search?text={text}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ItemDto>>() {},
                text
        );
        log.info("Найдено {} вещей по запросу: {}", response.getBody() != null ? response.getBody().size() : 0, text);
        return response;
    }

    @CacheEvict(value = "items", allEntries = true)
    public ResponseEntity<ItemDto> create(Long userId, ItemDto itemDto) {
        log.info("Отправка запроса на создание новой вещи для пользователя с ID={}, данные: {}", userId, itemDto);
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto);
        ResponseEntity<ItemDto> response = restTemplate.exchange(
                "?userId={userId}",
                HttpMethod.POST,
                requestEntity,
                ItemDto.class,
                userId
        );
        log.info("Создана новая вещь: {}", response.getBody());
        return response;
    }

    @CacheEvict(value = "items", key = "#itemId")
    public ResponseEntity<ItemDto> update(Long itemId, Long userId, Map<String, Object> updates) {
        log.info("Отправка запроса на обновление вещи с ID={} для пользователя с ID={}, данные: {}", itemId, userId, updates);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updates);
        ResponseEntity<ItemDto> response = restTemplate.exchange(
                "/{itemId}?userId={userId}",
                HttpMethod.PATCH,
                requestEntity,
                ItemDto.class,
                itemId,
                userId
        );
        log.info("Вещь с ID={} обновлена: {}", itemId, response.getBody());
        return response;
    }

    @CacheEvict(value = "items", key = "#itemId")
    public ResponseEntity<CommentDto> addComment(Long itemId, Long userId, CommentDto commentDto) {
        log.info("Отправка запроса на добавление комментария к вещи с ID={} от пользователя с ID={}, текст: {}",
                itemId, userId, commentDto.getText());
        HttpEntity<CommentDto> requestEntity = new HttpEntity<>(commentDto);
        ResponseEntity<CommentDto> response = restTemplate.exchange(
                "/{itemId}/comment?userId={userId}",
                HttpMethod.POST,
                requestEntity,
                CommentDto.class,
                itemId,
                userId
        );
        log.info("Добавлен комментарий: {}", response.getBody());
        return response;
    }
}
