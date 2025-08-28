package ru.practicum.gateway.entity.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserClient {
    private final RestTemplate restTemplate;

    public UserClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        log.info("Инициализация UserClient с URL сервера: {}", serverUrl);
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                .build();
    }

    @Cacheable(value = "users", key = "'all'")
    public ResponseEntity<List<UserDto>> getUserList() {
        log.info("Отправка запроса на получение списка всех пользователей");
        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
                "/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDto>>() {}
        );
        log.info("Получен список из {} пользователей", response.getBody() != null ? response.getBody().size() : 0);
        return response;
    }

    @Cacheable(value = "users", key = "#id")
    public ResponseEntity<UserDto> getUserDto(Long id) {
        log.info("Отправка запроса на получение пользователя с id={}", id);
        ResponseEntity<UserDto> response = restTemplate.getForEntity("/{id}", UserDto.class, id);
        log.info("Получен пользователь с id={}: {}", id, response.getBody());
        return response;
    }

    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<UserDto> create(UserDto userDto) {
        log.info("Отправка запроса на создание пользователя: {}", userDto);
        HttpEntity<UserDto> request = new HttpEntity<>(userDto);
        ResponseEntity<UserDto> response = restTemplate.exchange("", HttpMethod.POST, request, UserDto.class);
        log.info("Создан пользователь: {}", response.getBody());
        return response;
    }

    @CacheEvict(value = "users", key = "#id")
    public ResponseEntity<UserDto> update(Long id, Map<String, Object> updates) {
        log.info("Отправка запроса на обновление пользователя с id={}, данные: {}", id, updates);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updates);
        ResponseEntity<UserDto> response = restTemplate.exchange("/{id}", HttpMethod.PATCH, requestEntity, UserDto.class, id);
        log.info("Пользователь с id={} обновлён: {}", id, response.getBody());
        return response;
    }

    @CacheEvict(value = "users", key = "#id")
    public ResponseEntity<Void> delete(Long id) {
        log.info("Отправка запроса на удаление пользователя с id={}", id);
        ResponseEntity<Void> response = restTemplate.exchange("/{id}", HttpMethod.DELETE, null, Void.class, id);
        log.info("Пользователь с id={} удалён", id);
        return response;
    }
}
