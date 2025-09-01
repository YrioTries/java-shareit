package ru.practicum.shareit.entity.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.entity.booking.dto.BookingMapper;
import ru.practicum.shareit.entity.booking.dto.BookingRequestDto;
import ru.practicum.shareit.entity.booking.dto.BookingResponseDto;
import ru.practicum.shareit.entity.booking.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.entity.item.services.ItemService;
import ru.practicum.shareit.entity.item.model.Item;
import ru.practicum.shareit.entity.user.UserService;
import ru.practicum.shareit.entity.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        log.info("Создание нового бронирования для пользователя с ID={}, данные: {}", userId, bookingRequestDto);
        User booker = userService.getUser(userId);
        Item item = itemService.getItemById(bookingRequestDto.getItemId());
        validateBooking(bookingRequestDto, item, booker);
        Booking booking = bookingMapper.toBooking(bookingRequestDto, item, booker);
        booking.setStatus(Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        BookingResponseDto responseDto = bookingMapper.toResponseDto(savedBooking);
        log.info("Создано новое бронирование с ID={}", savedBooking.getId());
        return responseDto;
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        log.info("Подтверждение/отклонение бронирования с ID={} пользователем с ID={}, статус: {}",
                bookingId, userId, approved ? "подтверждено" : "отклонено");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.error("Попытка подтверждения бронирования пользователем, который не является владельцем вещи");
            throw new ValidationException("Подтверждать бронирование может только владелец вещи");
        }
        if (booking.getStatus() != Status.WAITING) {
            log.error("Бронирование с ID={} уже было обработано", bookingId);
            throw new ValidationException("Бронирование уже было обработано");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        BookingResponseDto responseDto = bookingMapper.toResponseDto(updatedBooking);
        log.info("Бронирование с ID={} обновлено, новый статус: {}", bookingId, updatedBooking.getStatus());
        return responseDto;
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        log.info("Получение информации о бронировании с ID={} для пользователя с ID={}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            log.error("Попытка просмотра бронирования пользователем, который не является автором или владельцем");
            throw new ValidationException("Просмотр бронирования доступен только автору или владельцу");
        }
        BookingResponseDto responseDto = bookingMapper.toResponseDto(booking);
        log.info("Получено бронирование с ID={}", bookingId);
        return responseDto;
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state, Integer from, Integer size) {
        log.info("Получение списка бронирований пользователя с ID={}, состояние: {}, с {} по {}",
                userId, state, from, size);
        userService.validateUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();
        List<BookingResponseDto> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentByBookerId(userId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            case "PAST":
                bookings = bookingRepository.findPastByBookerId(userId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByBookerId(userId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            case "WAITING":
            case "REJECTED":
                Status status = Status.valueOf(state.toUpperCase());
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, status, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            default:
                log.error("Неизвестное состояние бронирования: {}", state);
                throw new ValidationException("Unknown state: " + state);
        }
        log.info("Найдено {} бронирований для пользователя с ID={}", bookings.size(), userId);
        return bookings;
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
        log.info("Получение списка бронирований для вещей владельца с ID={}, состояние: {}, с {} по {}",
                ownerId, state, from, size);
        userService.validateUserExists(ownerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();
        List<BookingResponseDto> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentByOwnerId(ownerId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            case "PAST":
                bookings = bookingRepository.findPastByOwnerId(ownerId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByOwnerId(ownerId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            case "WAITING":
            case "REJECTED":
                Status status = Status.valueOf(state.toUpperCase());
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
                break;
            default:
                log.error("Неизвестное состояние бронирования: {}", state);
                throw new ValidationException("Unknown state: " + state);
        }
        log.info("Найдено {} бронирований для вещей владельца с ID={}", bookings.size(), ownerId);
        return bookings;
    }

    private void validateBooking(BookingRequestDto bookingDto, Item item, User booker) {
        log.info("Валидация данных для бронирования: {}", bookingDto);
        if (userService.getUser(booker.getId()) == null) {
            log.error("Пользователя с ID={} не существует", booker.getId());
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        if (itemService.getItemById(item.getId()) == null) {
            log.error("Вещи с ID={} не существует", item.getId());
            throw new NotFoundException("Вещи с таким id не существует");
        }
        if (item.getOwner().getId().equals(booker.getId())) {
            log.error("Владелец вещи с ID={} пытается забронировать свою вещь", item.getId());
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }
        if (!item.getAvailable()) {
            log.error("Вещь с ID={} недоступна для бронирования", item.getId());
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (bookingRepository.existsApprovedBookingsForItemBetweenDates(
                item.getId(), bookingDto.getStart(), bookingDto.getEnd())) {
            log.error("Вещь с ID={} уже забронирована на указанные даты", item.getId());
            throw new ValidationException("Вещь уже забронирована на указанные даты");
        }
        log.info("Валидация данных для бронирования пройдена успешно");
    }
}
