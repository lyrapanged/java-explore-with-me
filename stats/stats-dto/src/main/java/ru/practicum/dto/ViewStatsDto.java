package ru.practicum.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {

    String app;
    String uri;
    Long hits;
}
