package ru.practicum.explore.compilation.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompilationDtoUpdate {

    private List<Long> events;
    private String title;
    private Boolean pinned;
}
