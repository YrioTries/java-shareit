package ru.practicum.shareit.entity.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.entity.item.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequest() {
        return new ItemRequestDto();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getRequests() {
        return new ArrayList<ItemRequestDto>();
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getRequestsAll() {
        return new ArrayList<ItemRequestDto>();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void addRequest() {
    }
}
