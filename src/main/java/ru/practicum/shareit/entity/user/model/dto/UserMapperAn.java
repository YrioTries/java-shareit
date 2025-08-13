package ru.practicum.shareit.entity.user.model.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.entity.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapperAn {
    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
