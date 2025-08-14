package ru.practicum.shareit.entity.user.model.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.entity.user.model.User;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
