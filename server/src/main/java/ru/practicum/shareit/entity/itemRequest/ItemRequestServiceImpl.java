package ru.practicum.shareit.entity.itemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.entity.item.ItemRepository;
import ru.practicum.shareit.entity.item.model.ItemResponseDto;
import ru.practicum.shareit.entity.item.model.Item;
import ru.practicum.shareit.entity.user.UserRepository;
import ru.practicum.shareit.entity.user.model.User;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Получение запроса на вещь с id={}", requestId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден!"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Запрос не найден"));

        List<Item> relatedItems = itemRepository.findByRequestId(requestId);
        log.info("Найдено {} вещей, связанных с запросом id={}", relatedItems.size(), requestId);
        List<ItemResponseDto> itemResponseDtos = getItemResponses(relatedItems);
        ItemRequestDto result = itemRequestMapper.toItemRequestDtoWithItems(request, itemResponseDtos);
        log.info("Получен запрос на вещь: {}", result);
        return result;
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.info("Получение всех запросов пользователя с id={}", userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        log.info("Найдено {} запросов для пользователя с id={}", requests.size(), userId);
        return requests.stream()
                .map(request -> {
                    List<ItemResponseDto> answers = getItemResponses(itemRepository.findByRequestId(request.getId()));
                    ItemRequestDto requestDto = itemRequestMapper.toItemRequestDtoWithItems(request, answers);
                    log.info("Обработан запрос с id={}", request.getId());
                    return requestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        log.info("Получение всех запросов, кроме пользователя с id={}, начиная с {} по {}", userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId, pageable);
        log.info("Найдено {} запросов", requests.size());
        return requests.stream()
                .map(request -> {
                    List<ItemResponseDto> answers = itemRepository.findByRequestId(request.getId())
                            .stream()
                            .map(item -> new ItemResponseDto(
                                    item.getId(),
                                    item.getName(),
                                    item.getOwner().getId()
                            ))
                            .collect(Collectors.toList());
                    ItemRequestDto requestDto = itemRequestMapper.toItemRequestDtoWithItems(request, answers);
                    log.info("Обработан запрос с id={}", request.getId());
                    return requestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Создание нового запроса на вещь от пользователя с id={}", userId);
        ItemRequest request = itemRequestMapper.toItemRequest(itemRequestDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден!"));

        request.setRequester(owner);
        request.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);
        log.info("Создан запрос на вещь с id={}", savedRequest.getId());
        return itemRequestMapper.toItemRequestDtoWithItems(savedRequest, List.of());
    }

    private List<ItemResponseDto> getItemResponses(List<Item> relatedItems) {
        log.info("Формирование списка ответов для {} вещей", relatedItems.size());
        return relatedItems.stream()
                .map(item -> {
                    ItemResponseDto itemResponseDto = new ItemResponseDto(
                            item.getId(),
                            item.getName(),
                            item.getOwner().getId()
                    );
                    log.debug("Создан ответ для вещи с id={}", item.getId());
                    return itemResponseDto;
                })
                .collect(Collectors.toList());
    }
}
