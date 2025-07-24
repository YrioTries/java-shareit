package ru.practicum.shareit.entity.booking.dto;

import ru.practicum.shareit.entity.booking.Booking;

import ru.practicum.shareit.entity.item.model.Item;

import ru.practicum.shareit.entity.item.model.dto.ItemMapper;
import ru.practicum.shareit.entity.user.model.User;
import ru.practicum.shareit.entity.user.model.dto.UserMapper;

public class BookingMapper {

    public static Booking toBooking(BookingRequestDto bookingRequestDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }
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
