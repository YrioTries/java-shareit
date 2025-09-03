package ru.practicum.server.shareit.entity.itemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getRequestById(Long userId, Long requestId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId,Integer from,Integer size);

    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);
}
