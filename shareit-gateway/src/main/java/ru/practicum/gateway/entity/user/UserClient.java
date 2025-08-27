package ru.practicum.gateway.entity.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
@AllArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    public UserClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                .build();
    }

    public ResponseEntity<List<UserDto>> getUserList() {
        return restTemplate.exchange("/", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<UserDto>>() {});
    }

    public ResponseEntity<UserDto> getUserDto(Long id) {
        return restTemplate.getForEntity("/{id}", UserDto.class, id);
    }

    public ResponseEntity<UserDto> create(UserDto userDto) {
        HttpEntity<UserDto> request = new HttpEntity<>(userDto);
        return restTemplate.exchange("/", HttpMethod.POST, request, UserDto.class);
    }

    public ResponseEntity<UserDto> update(Long id, Map<String, Object> updates) {
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updates);
        return restTemplate.exchange("/{id}", HttpMethod.PATCH, requestEntity, UserDto.class, id);
    }

    public ResponseEntity<Void> delete(Long id) {
        return restTemplate.exchange("/{id}", HttpMethod.DELETE, null, Void.class, id);
    }
}
