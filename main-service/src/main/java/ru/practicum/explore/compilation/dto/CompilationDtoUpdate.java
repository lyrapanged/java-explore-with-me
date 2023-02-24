package ru.practicum.explore.compilation.dto;

import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompilationDtoUpdate {

    private Set<Long> events;
    private String title;
    private Boolean pinned;
}
