package ru.practicum.explore.request.service;

import ru.practicum.explore.request.dto.RequestDto;
import ru.practicum.explore.request.dto.RequestDtoCollection;
import ru.practicum.explore.request.dto.RequestDtoStatusUpdate;

import java.util.List;

public interface RequestService {

    RequestDto create(long userId, long eventId);

    RequestDtoCollection updateState(Long eventId, Long userId, RequestDtoStatusUpdate dto);

    RequestDto updateStateCancel(Long userId, Long requestId);

    List<RequestDto> get(Long userId);

    List<RequestDto> getAllByOwner(Long eventId, Long userId);

}
