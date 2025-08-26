package ru.practicum.shareit.server.entity.booking;

import ru.practicum.shareit.server.entity.booking.dto.BookingRequestDto;
import ru.practicum.shareit.server.entity.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long userId);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    List<BookingResponseDto> getUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, Integer from, Integer size);
}
