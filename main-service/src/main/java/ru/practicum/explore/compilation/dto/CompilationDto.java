package ru.practicum.explore.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.event.dto.EventDtoCompilation;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompilationDto {

    private List<EventDtoCompilation> events;
    private Long id;
    private boolean pinned;
    private String title;
}
