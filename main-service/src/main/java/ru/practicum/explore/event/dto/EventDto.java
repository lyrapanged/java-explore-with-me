package ru.practicum.explore.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.event.model.Event.State;
import ru.practicum.explore.event.model.Location;
import ru.practicum.explore.user.dto.UserDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventDto {

    private Long id;
    private String annotation;
    private CategoryDto category;
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private int confirmedRequest;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private State state;
    private String title;
}
