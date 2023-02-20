package ru.practicum.explore.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.mapper.UserMapper;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto user) {
        return userMapper.userToUserDto(userRepository.save(userMapper.userDtoToUser(user)));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(userMapper::userToUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllById(ids).stream()
                    .map(userMapper::userToUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkUserExist(id);
        userRepository.deleteById(id);
    }

    private User checkUserExist(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User id = %s not found", id)));
    }
}
