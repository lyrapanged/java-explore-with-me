package ru.practicum.explore.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompilationDtoNew {

    private List<Long> events;
    @NotBlank
    private String title;
    private boolean pinned;
}
