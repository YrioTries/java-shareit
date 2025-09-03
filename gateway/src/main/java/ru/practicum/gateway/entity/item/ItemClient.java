package ru.practicum.gateway.entity.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;

import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.base.BaseClient;

import java.util.Map;

@Slf4j
@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

//    @Cacheable(value = "items", key = "#itemId")
    public ResponseEntity<Object> getItemById(Long itemId) {
        log.info("Отправка запроса на получение информации о вещи с ID={}", itemId);
        return get("/" + itemId);
    }

//    @Cacheable(value = "items", key = "'user_' + #userId")
    public ResponseEntity<Object> getItemByUserId(Long userId) {
        log.info("Отправка запроса на получение списка вещей пользователя с ID={}", userId);
        return get("", userId);
    }

//    @Cacheable(value = "items", key = "'search_' + #text")
    public ResponseEntity<Object> searchText(String text) {
        log.info("Отправка запроса на поиск вещей по тексту: {}", text);
        if (text == null || text.isBlank()) {
            return get("/search");
        }
        return get("/search?text=" + text);
    }

//    @CacheEvict(value = "items", allEntries = true)
    public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
        log.info("Отправка запроса на создание новой вещи для пользователя с ID={}, данные: {}", userId, itemDto);
        return post("", userId, itemDto);
    }

//    @CacheEvict(value = "items", key = "#itemId")
    public ResponseEntity<Object> update(Long itemId, Long userId, Map<String, Object> updates) {
        log.info("Отправка запроса на обновление вещи с ID={} для пользователя с ID={}, данные: {}", itemId, userId, updates);
        return patch("/" + itemId, userId, updates);
    }

//    @CacheEvict(value = "items", key = "#itemId")
    public ResponseEntity<Object> addComment(Long itemId, Long userId, CommentDto commentDto) {
        log.info("Отправка запроса на добавление комментария к вещи с ID={} от пользователя с ID={}, текст: {}",
                itemId, userId, commentDto.getText());
        return post("/" + itemId + "/comment", userId, commentDto);

    }

//    @CacheEvict(value = "items", key = "#itemId")
    public ResponseEntity<Object> deleteItem(Long itemId) {
        return delete("/" + itemId);
    }

//    @Cacheable(value = "items", key = "'item_' + #itemId + '_user_' + #userId")
    public ResponseEntity<Object> getItemDtoWithBookingsAndComments(Long itemId, Long userId) {
        log.info("Запрос информации о вещи с ID={} для пользователя {}", itemId, userId);
        log.debug("Полный URL: {}/{}", rest.getUriTemplateHandler().toString(), itemId);

        ResponseEntity<Object> response = getWithHeaders("/" + itemId, userId);

        log.debug("Ответ от сервера: статус {}, тело: {}",
                response.getStatusCode(), response.getBody());

        return response;
    }
}
