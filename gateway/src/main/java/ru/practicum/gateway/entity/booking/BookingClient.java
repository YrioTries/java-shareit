package ru.practicum.gateway.entity.booking;

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

@Service
public class BookingClient  extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    @CacheEvict(value = "bookings", allEntries = true)
    public ResponseEntity<Object> createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        return post("", userId, bookingRequestDto);
    }

    @CacheEvict(value = "bookings", key = "#bookingId")
    public ResponseEntity<Object> approveBooking(Long bookingId, Boolean approved, Long userId) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    @Cacheable(value = "bookings", key = "#bookingId")
    public ResponseEntity<Object> getBookingById(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    @Cacheable(value = "bookings", key = "'user_' + #userId + '_state_' + #state + '_from_' + #from + '_size_' + #size")
    public ResponseEntity<Object> getUserBookings(Long userId, Status state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    @Cacheable(value = "bookings", key = "'user_' + #ownerId + '_state_' + #state + '_from_' + #from + '_size_' + #size")
    public ResponseEntity<Object> getOwnerBookings(Long ownerId, Status state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }

    @CacheEvict(value = "bookings", key = "#bookingId")
    public ResponseEntity<Object> deleteBooking(long userId, Long bookingId) {
        return delete("/" + bookingId, userId);
    }

}
