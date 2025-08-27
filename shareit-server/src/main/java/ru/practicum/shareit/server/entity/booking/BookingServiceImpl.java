package ru.practicum.shareit.server.entity.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.entity.booking.dto.BookingMapper;
import ru.practicum.shareit.server.entity.booking.dto.BookingRequestDto;
import ru.practicum.shareit.server.entity.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.entity.booking.enums.Status;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.entity.item.services.ItemService;
import ru.practicum.shareit.server.entity.item.model.Item;
import ru.practicum.shareit.server.entity.user.UserService;
import ru.practicum.shareit.server.entity.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        User booker = userService.getUser(userId);
        Item item = itemService.getItemById(bookingRequestDto.getItemId());

        validateBooking(bookingRequestDto, item, booker);

        Booking booking = bookingMapper.toBooking(bookingRequestDto, item, booker);
        booking.setStatus(Status.WAITING);

        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Подтверждать бронирование может только владелец вещи");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Бронирование уже было обработано");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Просмотр бронирования доступен только автору или владельцу");
        }
        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state, Integer from, Integer size) {
        userService.validateUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingRepository.findCurrentByBookerId(userId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findPastByBookerId(userId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findFutureByBookerId(userId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            case "WAITING":
            case "REJECTED":
                Status status = Status.valueOf(state.toUpperCase());
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, status, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            case "ALL":
                return bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
        userService.validateUserExists(ownerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingRepository.findCurrentByOwnerId(ownerId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findPastByOwnerId(ownerId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findFutureByOwnerId(ownerId, now, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            case "WAITING":
            case "REJECTED":
                Status status = Status.valueOf(state.toUpperCase());
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            case "ALL":
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable).stream()
                        .map(bookingMapper::toResponseDto)
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    private void validateBooking(BookingRequestDto bookingDto, Item item, User booker) {
        if (userService.getUser(booker.getId()) == null) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        if (itemService.getItemById(item.getId()) == null) {
            throw new NotFoundException("Вещи с таким id не существует");
        }
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (bookingRepository.existsApprovedBookingsForItemBetweenDates(
                item.getId(), bookingDto.getStart(), bookingDto.getEnd())) {
            throw new ValidationException("Вещь уже забронирована на указанные даты");
        }
    }
}
