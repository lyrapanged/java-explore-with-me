package ru.practicum.explore.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto user);
}
