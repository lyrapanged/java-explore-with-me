package ru.practicum.explore.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;


@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(CREATED)
    public UserDto create(@RequestBody @Valid UserDto user) {
        log.info("Creating user with email {}", user.getEmail());
        return userService.create(user);
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(required = false) List<Long> ids,
                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Getting users");
        Pageable pageable = PageRequest.of(from / size, size);
        return userService.getAll(ids, pageable);
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@Positive @PathVariable("userId") Long id) {
        log.info("Deleting user with userID = {}", id);
        userService.delete(id);
    }
}
