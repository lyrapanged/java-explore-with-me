package ru.practicum.explore.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.request.model.Request.RequestStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestDtoStatusUpdate {

    private List<Long> requestIds;
    private RequestStatus status;
}

