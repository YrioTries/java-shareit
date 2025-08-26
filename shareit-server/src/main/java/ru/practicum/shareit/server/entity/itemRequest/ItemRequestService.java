package ru.practicum.shareit.server.entity.itemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getRequestById(Long requestId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId,Integer from,Integer size);

    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);
}
