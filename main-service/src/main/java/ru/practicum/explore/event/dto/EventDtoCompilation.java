package ru.practicum.explore.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.user.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDtoCompilation {

    private Long id;
    private String annotation;
    private Category category;
    private Long confirmedRequests;
    private User initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
