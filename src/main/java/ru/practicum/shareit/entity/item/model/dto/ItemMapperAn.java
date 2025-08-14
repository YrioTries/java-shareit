package ru.practicum.shareit.entity.item.model.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.entity.booking.dto.BookingDto;
import ru.practicum.shareit.entity.booking.dto.BookingMapperAn;
import ru.practicum.shareit.entity.comment.model.CommentDto;
import ru.practicum.shareit.entity.comment.model.CommentMapperAn;
import ru.practicum.shareit.entity.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {BookingMapperAn.class, CommentMapperAn.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapperAn {
    // Автоматический маппинг для простых полей
    ItemDto toItemDto(Item item);

    // Явный маппинг для вложенных объектов
    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "lastBooking", target = "lastBooking")
    @Mapping(source = "nextBooking", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    ItemDto toItemDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments);

    // Обратный маппинг
    Item toItem(ItemDto itemDto);
}
