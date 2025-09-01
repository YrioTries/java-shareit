package ru.practicum.shareit.server.entity.item.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.entity.booking.Booking;
import ru.practicum.shareit.server.entity.booking.BookingRepository;
import ru.practicum.shareit.server.entity.booking.dto.BookingDto;
import ru.practicum.shareit.server.entity.booking.dto.BookingMapper;
import ru.practicum.shareit.server.entity.comment.CommentRepository;
import ru.practicum.shareit.server.entity.comment.model.Comment;
import ru.practicum.shareit.server.entity.comment.model.CommentDto;
import ru.practicum.shareit.server.entity.comment.model.CommentMapper;
import ru.practicum.shareit.server.entity.item.ItemRepository;
import ru.practicum.shareit.server.entity.item.dto.ItemMapper;
import ru.practicum.shareit.server.entity.itemRequest.ItemRequest;
import ru.practicum.shareit.server.entity.itemRequest.ItemRequestRepository;
import ru.practicum.shareit.server.entity.user.model.User;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.entity.item.dto.ItemDto;
import ru.practicum.shareit.server.entity.item.model.Item;
import ru.practicum.shareit.server.entity.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto getItemDtoById(Long id) {
        log.info("Получение информации о вещи с ID={}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + id + " не найдена"));
        ItemDto itemDto = itemMapper.toItemDto(item);
        log.info("Получена информация о вещи: {}", itemDto);
        return itemDto;
    }

    @Override
    public Item getItemById(Long id) {
        log.info("Получение сущности вещи с ID={}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + id + " не найдена"));
        log.info("Получена сущность вещи: {}", item);
        return item;
    }

    @Override
    public List<ItemDto> getItemByUserId(Long userId) {
        log.info("Получение списка вещей пользователя с ID={}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("Пользователь с ID={} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        List<ItemDto> items = itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Получено {} вещей для пользователя с ID={}", items.size(), userId);
        return items;
    }

    @Override
    public List<ItemDto> searchText(String text) {
        log.info("Поиск вещей по тексту: {}", text);
        if (text == null || text.isBlank()) {
            log.info("Поисковый запрос пуст, возвращается пустой список");
            return List.of();
        }
        List<ItemDto> items = itemRepository.searchAvailableItems(text.toLowerCase()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Найдено {} вещей по запросу: {}", items.size(), text);
        return items;
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Создание новой вещи для пользователя с ID={}, данные: {}", userId, itemDto);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        ItemRequest request = Optional.ofNullable(itemDto.getRequestId())
                .flatMap(itemRequestRepository::findById)
                .orElse(null);

        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);
        item.setRequest(request);
        Item savedItem = itemRepository.save(item);
        ItemDto savedItemDto = itemMapper.toItemDto(savedItem);
        log.info("Создана новая вещь: {}", savedItemDto);
        return savedItemDto;
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, Map<String, Object> updates) {
        log.info("Обновление вещи с ID={} для пользователя с ID={}, данные: {}", itemId, userId, updates);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            log.error("Попытка редактирования вещи пользователем, который не является владельцем");
            throw new NotFoundException("Редактировать вещь может только владелец");
        }
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    if (value != null) {
                        log.debug("Обновление названия вещи на {}", value);
                        item.setName((String) value);
                    }
                    break;
                case "description":
                    if (value != null) {
                        log.debug("Обновление описания вещи на {}", value);
                        item.setDescription((String) value);
                    }
                    break;
                case "available":
                    if (value != null) {
                        log.debug("Обновление доступности вещи на {}", value);
                        item.setAvailable((Boolean) value);
                    }
                    break;
            }
        });
        Item updatedItem = itemRepository.save(item);
        ItemDto updatedItemDto = itemMapper.toItemDto(updatedItem);
        log.info("Вещь обновлена: {}", updatedItemDto);
        return updatedItemDto;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Добавление комментария к вещи с ID={} от пользователя с ID={}, текст: {}",
                itemId, userId, commentDto.getText());
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        List<Booking> userBookings = bookingRepository.findAllByUserBookings(userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new NotFoundException("У пользователя с id " + userId + " должно быть хотя бы одно бронирование предмета с id " + itemId);
        }

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now())) {
            log.error("Пользователь с ID={} не бронировал вещь с ID={}", userId, itemId);
            throw new ValidationException("Пользователь не бронировал эту вещь");
        }

        Comment comment = new Comment(null,
                commentDto.getText(),
                item,
                author,
                LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        CommentDto savedCommentDto = commentMapper.toCommentDto(savedComment);
        log.info("Добавлен комментарий: {}", savedCommentDto);
        return savedCommentDto;
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


}
