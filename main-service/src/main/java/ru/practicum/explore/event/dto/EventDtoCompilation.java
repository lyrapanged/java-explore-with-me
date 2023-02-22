package ru.practicum.explore.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.user.dto.UserDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDtoCompilation {

    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private UserDto initiatorDto;
    private Boolean paid;
    private String title;
    private Integer views;
}
