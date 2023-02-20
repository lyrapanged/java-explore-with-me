package ru.practicum.explore.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestDtoCollection {

    List<RequestDto> confirmedRequests;

    List<RequestDto> rejectedRequests;
}
