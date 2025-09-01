package ru.practicum.entity.user;

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
import ru.practicum.base.BaseClient;

import java.util.Map;

@Slf4j
@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    @Cacheable(value = "users", key = "'all'")
    public ResponseEntity<Object> getUserList() {
        log.info("Отправка запроса на получение списка всех пользователей");
        ResponseEntity<Object> response = get("");
        log.info("Получен список пользователей");
        return response;
    }

    @Cacheable(value = "users", key = "#id")
    public ResponseEntity<Object> getUserDto(Long id) {
        log.info("Отправка запроса на получение пользователя с id={}", id);
        return get("/" + id);
    }

    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<Object> create(UserDto userDto) {
        log.info("Отправка запроса на создание пользователя: {}", userDto);
        return post("", userDto);
    }

    @CacheEvict(value = "users", key = "#userId")
    public ResponseEntity<Object> update(Long userId, Map<String, Object> updates) {
        log.info("Отправка запроса на обновление пользователя с ID={}, данные: {}", userId, updates);
        return patch("/" + userId, updates);
    }


    @CacheEvict(value = "users", key = "#id")
    public ResponseEntity<Object> delete(Long id) {
        log.info("Отправка запроса на удаление пользователя с id={}", id);
        return delete("/" + id);
    }
}
