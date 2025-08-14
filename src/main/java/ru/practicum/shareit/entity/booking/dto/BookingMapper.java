package ru.practicum.shareit.entity.booking.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.entity.booking.Booking;
import ru.practicum.shareit.entity.booking.enums.Status;
import ru.practicum.shareit.entity.item.model.Item;
import ru.practicum.shareit.entity.item.model.dto.ItemMapperAn;
import ru.practicum.shareit.entity.user.model.User;
import ru.practicum.shareit.entity.user.model.dto.UserMapperAn;

@Mapper(componentModel = "spring",
        uses = {ItemMapperAn.class, UserMapperAn.class},
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
