package ru.practicum.gateway.entity.item_request;

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

@Service
public class ItemRequestClient {

    private final RestTemplate restTemplate;

    public ItemRequestClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/requests"))
                .build();
    }

    @Cacheable(value = "itemRequests", key = "#requestId")
    public ResponseEntity<ItemRequestDto> getRequestById(Long requestId) {
        return restTemplate.getForEntity("/{requestId}", ItemRequestDto.class, requestId);
    }


    @Cacheable(value = "itemRequests", key = "'user_' + #userId")
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                "/",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemRequestDto>>() {}
        );
    }

    @Cacheable(value = "itemRequests", key = "'all_user_' + #userId + '_from_' + #from + '_size_' + #size")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(Long userId, Integer from, Integer size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = String.format("/all?from=%d&size=%d", from, size);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemRequestDto>>() {}
        );
    }

    @CacheEvict(value = "itemRequests", allEntries = true)
    public ResponseEntity<ItemRequestDto> createRequest(Long userId, ItemRequestDto itemRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        HttpEntity<ItemRequestDto> entity = new HttpEntity<>(itemRequestDto, headers);
        return restTemplate.exchange(
                "/",
                HttpMethod.POST,
                entity,
                ItemRequestDto.class
        );
    }
}
