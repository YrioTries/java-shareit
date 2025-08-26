package ru.practicum.shareit.server.entity.itemRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.server.entity.item.ItemRepository;
import ru.practicum.shareit.server.entity.item.model.ItemResponseDto;
import ru.practicum.shareit.server.entity.item.model.Item;
import ru.practicum.shareit.server.entity.user.UserRepository;
import ru.practicum.shareit.server.entity.user.model.User;
import ru.practicum.shareit.server.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto getRequestById(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new RuntimeException("Request not found")
        );

        List<Item> relatedItems = itemRepository.findByRequestId(requestId);
        List<ItemResponseDto> itemResponseDtos = getItemResponses(relatedItems);

        return itemRequestMapper.toItemRequestDtoWithItems(request, itemResponseDtos);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<ItemResponseDto> answers = getItemResponses(itemRepository.findByRequestId(request.getId()));
                    return itemRequestMapper.toItemRequestDtoWithItems(request, answers);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId, pageable);
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
                    return itemRequestMapper.toItemRequestDtoWithItems(request, answers);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequestDto.getDescription());
        User user;
         if (userRepository.findById(userId).isPresent())
             user = userRepository.findById(userId).get();
         else
             throw new NotFoundException("Пользователя с таким id нет в бд");

        request.setRequester(userRepository.getReferenceById(userId));
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(request);
        return itemRequestMapper.toItemRequestDtoWithItems(savedRequest, List.of());
    }

    private List<ItemResponseDto> getItemResponses(List<Item> relatedItems) {
        return relatedItems.stream()
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getOwner().getId()
                ))
                .collect(Collectors.toList());

    }

}
