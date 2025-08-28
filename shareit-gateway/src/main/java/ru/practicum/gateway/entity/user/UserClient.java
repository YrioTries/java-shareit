package ru.practicum.gateway.entity.user;

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


@Service
public class UserClient {

    private final RestTemplate restTemplate;

    public UserClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                .build();
    }

    @Cacheable(value = "users", key = "'all'")
    public ResponseEntity<List<UserDto>> getUserList() {
        return restTemplate.exchange(
                "/", // Корневой путь "/users" уже задан в DefaultUriBuilderFactory
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDto>>() {}
        );
    }

    @Cacheable(value = "users", key = "#id")
    public ResponseEntity<UserDto> getUserDto(Long id) {
        return restTemplate.getForEntity("/{id}", UserDto.class, id);
    }


    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<UserDto> create(UserDto userDto) {
        HttpEntity<UserDto> request = new HttpEntity<>(userDto);
        return restTemplate.exchange("/", HttpMethod.POST, request, UserDto.class);
    }

    @CacheEvict(value = "users", key = "#id")
    public ResponseEntity<UserDto> update(Long id, Map<String, Object> updates) {
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updates);
        return restTemplate.exchange("/{id}", HttpMethod.PATCH, requestEntity, UserDto.class, id);
    }

    @CacheEvict(value = "users", key = "#id")
    public ResponseEntity<Void> delete(Long id) {
        return restTemplate.exchange("/{id}", HttpMethod.DELETE, null, Void.class, id);
    }
}
