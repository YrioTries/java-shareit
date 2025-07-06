package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class UserDto {
    private Long id;
    private String name;
    @Email
    @NotBlank
    @NotEmpty
    private String email;

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.name = user.getName();
        dto.email = user.getEmail();
        return dto;
    }
}
