package ru.practicum.explore.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDtoCollection {

    List<RequestDto> confirmedRequests;

    List<RequestDto> rejectedRequests;
}
