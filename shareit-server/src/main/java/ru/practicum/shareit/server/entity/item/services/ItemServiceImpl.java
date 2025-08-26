package ru.practicum.shareit.server.entity.item.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.entity.booking.BookingRepository;
import ru.practicum.shareit.server.entity.booking.dto.BookingDto;
import ru.practicum.shareit.server.entity.booking.dto.BookingMapper;
import ru.practicum.shareit.server.entity.comment.CommentRepository;
import ru.practicum.shareit.server.entity.comment.model.Comment;
import ru.practicum.shareit.server.entity.comment.model.CommentDto;
import ru.practicum.shareit.server.entity.item.ItemRepository;
import ru.practicum.shareit.server.entity.item.dto.ItemMapper;
import ru.practicum.shareit.server.entity.user.model.User;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.entity.item.dto.ItemDto;
import ru.practicum.shareit.server.entity.item.model.Item;
import ru.practicum.shareit.server.entity.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto getItemDtoById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + id + " не найдена"));
        return itemMapper.toItemDto(item);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + id + " не найдена"));
    }

    @Override
    public List<ItemDto> getItemByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailableItems(text.toLowerCase()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, Map<String, Object> updates) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    if (value != null) item.setName((String) value);
                    break;
                case "description":
                    if (value != null) item.setDescription((String) value);
                    break;
                case "available":
                    if (value != null) item.setAvailable((Boolean) value);
                    break;
            }
        });
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException("Пользователь не бронировал эту вещь");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return toCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto getItemDtoWithBookingsAndComments(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        LocalDateTime now = LocalDateTime.now();
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository
                    .findLastBookingForItem(itemId, now, PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .map(bookingMapper::toBookingDto)
                    .orElse(null);

            nextBooking = bookingRepository
                    .findNextBookingForItem(itemId, now, PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .map(bookingMapper::toBookingDto)
                    .orElse(null);
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(comment -> {
                    CommentDto commentDto = new CommentDto();
                    commentDto.setId(comment.getId());
                    commentDto.setText(comment.getText());
                    commentDto.setAuthorName(comment.getAuthor().getName());
                    commentDto.setCreated(comment.getCreated());
                    return commentDto;
                }).collect(Collectors.toList());

        return itemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }


    private CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}
