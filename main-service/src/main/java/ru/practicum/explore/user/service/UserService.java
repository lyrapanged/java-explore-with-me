package ru.practicum.explore.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto user);

    List<UserDto> getAll(List<Long> ids, Pageable pageable);

    void delete(Long id);
}
