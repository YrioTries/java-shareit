package ru.practicum.gateway.entity.item_request;

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

@Slf4j
@Service
public class ItemRequestClient {
    private final RestTemplate restTemplate;

    public ItemRequestClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        log.info("Инициализация ItemRequestClient с URL сервера: {}", serverUrl);
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/requests"))
                .build();
    }

    @Cacheable(value = "itemRequests", key = "#requestId")
    public ResponseEntity<ItemRequestDto> getRequestById(Long requestId) {
        log.info("Отправка запроса на получение информации о запросе вещи с ID={}", requestId);
        ResponseEntity<ItemRequestDto> response = restTemplate.getForEntity("/{requestId}", ItemRequestDto.class, requestId);
        log.info("Получен запрос вещи с ID={}: {}", requestId, response.getBody());
        return response;
    }

    @Cacheable(value = "itemRequests", key = "'user_' + #userId")
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(Long userId) {
        log.info("Отправка запроса на получение списка запросов вещей пользователя с ID={}", userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<ItemRequestDto>> response = restTemplate.exchange(
                "",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemRequestDto>>() {}
        );
        log.info("Получено {} запросов вещей для пользователя с ID={}", response.getBody() != null ? response.getBody().size() : 0, userId);
        return response;
    }

    @Cacheable(value = "itemRequests", key = "'all_user_' + #userId + '_from_' + #from + '_size_' + #size")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(Long userId, Integer from, Integer size) {
        log.info("Отправка запроса на получение всех запросов вещей, кроме пользователя с ID={}, начиная с {} по {}", userId, from, size);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = String.format("/all?from=%d&size=%d", from, size);
        ResponseEntity<List<ItemRequestDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemRequestDto>>() {}
        );
        log.info("Получено {} запросов вещей для всех пользователей, кроме ID={}", response.getBody() != null ? response.getBody().size() : 0, userId);
        return response;
    }

    @CacheEvict(value = "itemRequests", allEntries = true)
    public ResponseEntity<ItemRequestDto> createRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Отправка запроса на создание нового запроса вещи от пользователя с ID={}, данные: {}", userId, itemRequestDto);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemRequestDto> entity = new HttpEntity<>(itemRequestDto, headers);
        ResponseEntity<ItemRequestDto> response = restTemplate.exchange(
                "",
                HttpMethod.POST,
                entity,
                ItemRequestDto.class
        );
        log.info("Создан запрос вещи: {}", response.getBody());
        return response;
    }
}
