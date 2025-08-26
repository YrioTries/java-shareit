package ru.practicum.shareit.server.entity.item.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.server.entity.booking.dto.BookingDto;
import ru.practicum.shareit.server.entity.booking.dto.BookingMapper;
import ru.practicum.shareit.server.entity.comment.model.CommentDto;
import ru.practicum.shareit.server.entity.comment.model.CommentMapper;
import ru.practicum.shareit.server.entity.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {BookingMapper.class, CommentMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "lastBooking", target = "lastBooking")
    @Mapping(source = "nextBooking", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    ItemDto toItemDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments);

    Item toItem(ItemDto itemDto);
}
