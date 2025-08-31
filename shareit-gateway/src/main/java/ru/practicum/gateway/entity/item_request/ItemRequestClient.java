package ru.practicum.gateway.entity.item_request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.base.BaseClient;

import java.util.Map;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    @Cacheable(value = "itemRequests", key = "#requestId")
    public ResponseEntity<Object> getRequestById(Long requestId) {
        log.info("Отправка запроса на получение информации о запросе вещи с ID={}", requestId);
        return get("", requestId);
    }


    @Cacheable(value = "itemRequests", key = "'user_' + #userId")
    public ResponseEntity<Object> getUserRequests(Long userId) {
        log.info("Отправка запроса на получение списка запросов вещей пользователя с ID={}", userId);
        return get("", userId);
    }

    @Cacheable(value = "itemRequests", key = "'all_user_' + #userId + '_from_' + #from + '_size_' + #size")
    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        log.info("Отправка запроса на получение всех запросов вещей, кроме пользователя с ID={}, начиная с {} по {}", userId, from, size);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all", userId, parameters);
    }

    @CacheEvict(value = "itemRequests", allEntries = true)
    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Отправка запроса на создание нового запроса вещи от пользователя с ID={}, данные: {}", userId, itemRequestDto);
        return post("", userId, itemRequestDto);
    }
}
