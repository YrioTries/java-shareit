package ru.practicum.server.shareit.entity.item.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.server.shareit.entity.booking.dto.BookingDto;
import ru.practicum.server.shareit.entity.booking.dto.BookingMapper;
import ru.practicum.server.shareit.entity.comment.model.CommentDto;
import ru.practicum.server.shareit.entity.comment.model.CommentMapper;
import ru.practicum.server.shareit.entity.item.model.Item;
import ru.practicum.server.shareit.entity.itemRequest.ItemRequestMapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {BookingMapper.class, CommentMapper.class, ItemRequestMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "lastBooking", target = "lastBooking")
    @Mapping(source = "nextBooking", target = "nextBooking")
    @Mapping(source = "item.request.id", target = "requestId")
    @Mapping(source = "comments", target = "comments")
    ItemDto toItemDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments);

    Item toItem(ItemDto itemDto);
}
