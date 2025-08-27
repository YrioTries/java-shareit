package ru.practicum.gateway.entity.booking;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

@Service
@AllArgsConstructor
public class BookingClient {

    private final RestTemplate restTemplate;

    public BookingClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .build();
    }

    public ResponseEntity<BookingResponseDto> createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        HttpEntity<BookingRequestDto> request = new HttpEntity<>(bookingRequestDto);
        return restTemplate.exchange("/",
                HttpMethod.POST,
                request,
                BookingResponseDto.class,
                userId);
    }

    public ResponseEntity<BookingResponseDto> approveBooking(Long bookingId, Boolean approved, Long userId) {
        String url = String.format("/%d?approved=%b&userId=%d", bookingId, approved, userId);

        return restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                BookingResponseDto.class
        );
    }


    public ResponseEntity<BookingResponseDto> getBookingById(Long bookingId, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());

        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("/{bookingId}",
                HttpMethod.GET,
                entity, BookingResponseDto.class,
                bookingId);
    }


    public ResponseEntity<List<BookingResponseDto>> getUserBookings(Long userId, String state, Integer from, Integer size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = String.format("/?state=%s&from=%d&size=%d", state, from, size);

        return restTemplate.exchange(url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<BookingResponseDto>>() {});
    }


    public ResponseEntity<List<BookingResponseDto>> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", ownerId.toString());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = String.format("/owner?state=%s&from=%d&size=%d", state, from, size);

        return restTemplate.exchange(url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<BookingResponseDto>>() {});
    }

}
