package ru.practicum.shareit.server.entity.booking.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.server.entity.booking.Booking;
import ru.practicum.shareit.server.entity.booking.enums.Status;
import ru.practicum.shareit.server.entity.item.model.Item;
import ru.practicum.shareit.server.entity.item.dto.ItemMapper;
import ru.practicum.shareit.server.entity.user.model.User;
import ru.practicum.shareit.server.entity.user.model.dto.UserMapper;

@Mapper(componentModel = "spring",
        uses = {ItemMapper.class, UserMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {

    @Mapping(source = "bookingRequestDto.id", target = "id")
    Booking toBooking(BookingRequestDto bookingRequestDto, Item item, User booker);

    BookingResponseDto toResponseDto(Booking booking);

    @Mapping(source = "item.id", target = "itemId")
    BookingRequestDto toRequestDto(Booking booking);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "booker.id", target = "bookerId")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapStatusToString")
    BookingDto toBookingDto(Booking booking);

    @Named("mapStatusToString")
    default String mapStatusToString(Status status) {
        return status != null ? status.name() : null;
    }
}
