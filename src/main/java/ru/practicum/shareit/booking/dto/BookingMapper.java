package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.user.model.dto.UserMapper;

public class BookingMapper {
    public static BookingResponseDto toResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())
        );
    }

    public static BookingRequestDto toRequestDto(Booking booking) {
        return new BookingRequestDto(
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId()
        );
    }

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus().name()
        );
    }
}
