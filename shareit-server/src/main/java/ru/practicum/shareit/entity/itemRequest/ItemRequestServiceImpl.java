package ru.practicum.shareit.entity.itemRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.entity.item.ItemRepository;
import ru.practicum.shareit.entity.item.dto.ItemResponseDto;
import ru.practicum.shareit.entity.item.model.Item;
import ru.practicum.shareit.entity.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto getRequestById(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new RuntimeException("Request not found")
        );

        List<Item> relatedItems = itemRepository.findByRequestId(requestId);
        List<ItemResponseDto> itemResponseDtos = getItemResponses(relatedItems);

        return mapToDto(request, itemResponseDtos);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<ItemResponse.ItemAnswerResponse> answers = itemRepository.findByRequestId(request.getId())
                            .stream()
                            .map(item -> new ItemResponse.ItemAnswerResponse(
                                    item.getId(),
                                    item.getName(),
                                    item.getOwner().getId()
                            ))
                            .collect(Collectors.toList());
                    return mapToDto(request, answers);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId, pageable);
        return requests.stream()
                .map(request -> {
                    List<ItemResponse.ItemAnswerResponse> answers = itemRepository.findByRequestId(request.getId())
                            .stream()
                            .map(item -> new ItemResponse.ItemAnswerResponse(
                                    item.getId(),
                                    item.getName(),
                                    item.getOwner().getId()
                            ))
                            .collect(Collectors.toList());
                    return mapToDto(request, answers);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequestDto.getDescription());
        request.setRequester(new User(userId)); // Предполагаем, что у вас есть доступ к объекту User
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(request);
        return mapToDto(savedRequest, List.of());
    }

    private ItemRequestDto mapToDto(ItemRequest request, List<ItemResponse.ItemAnswerResponse> answers) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(answers); // Предполагаем, что `ItemRequestDto` имеет поле `items` для хранения ответов
        return dto;
    }
}
