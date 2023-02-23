package ru.practicum.explore.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.Event.State;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.exception.RequestEventException;
import ru.practicum.explore.request.dto.RequestDto;
import ru.practicum.explore.request.dto.RequestDtoCollection;
import ru.practicum.explore.request.dto.RequestDtoStatusUpdate;
import ru.practicum.explore.request.mapper.RequestMapper;
import ru.practicum.explore.request.model.Request;
import ru.practicum.explore.request.model.Request.RequestStatus;
import ru.practicum.explore.request.repository.RequestRepository;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.explore.request.model.Request.RequestStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public RequestDto create(long userId, long eventId) {
        Event event = getEventOrThrow(eventId);
        List<Request> requests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        int participantCount = requestRepository.countRequestByStatusAndEventId(CONFIRMED, eventId);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= participantCount) {
            throw new RequestEventException("There are no seats but you are holding on");
        }
        if (event.getInitiator().getId() == userId) {
            throw new RequestEventException("You are the owner");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new RequestEventException("Event does not published");
        }
        if (!requests.isEmpty()) {
            throw new RequestEventException("You are already in this event");
        }
        Request request = new Request();
        if (!event.isRequestModeration()) {
            request.setStatus(CONFIRMED);
        } else {
            request.setStatus(PENDING);
        }
        User requester = getUserOrThrow(userId);
        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return requestMapper.requestToRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDtoCollection updateState(Long eventId, Long userId, RequestDtoStatusUpdate requestDtoStatusUpdate) {
        Event event = getEventOrThrow(eventId);
        getUserOrThrow(userId);
        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId, requestDtoStatusUpdate.getRequestIds());
        RequestStatus status = requestDtoStatusUpdate.getStatus();
        validatedRequest(event, requests, status);
        List<Long> ids = requestDtoStatusUpdate.getRequestIds();
        List<RequestDto> confirmed = requestRepository.findAllByStatusAndIdIn(CONFIRMED, ids).stream()
                .map(requestMapper::requestToRequestDto)
                .collect(toList());
        List<RequestDto> rejected = requestRepository.findAllByStatusAndIdIn(REJECTED, ids).stream()
                .map(requestMapper::requestToRequestDto)
                .collect(toList());
        return new RequestDtoCollection(confirmed, rejected);
    }

    @Override
    @Transactional
    public RequestDto updateStateCancel(Long requester, Long requestId) {
        Request request = getRequestOrThrow(requester, requestId);
        request.setStatus(CANCELED);
        return requestMapper.requestToRequestDto(request);
    }

    @Override
    public List<RequestDto> get(Long userId) {
        getUserOrThrow(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::requestToRequestDto)
                .collect(toList());
    }

    @Override
    public List<RequestDto> getAllByOwner(Long eventId, Long userId) {
        getEventOrThrow(eventId);
        getUserOrThrow(userId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::requestToRequestDto)
                .collect(toList());
    }

    private Event getEventOrThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Event id = %s not found", id)));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User id = %s not found", id)));
    }

    private Request getRequestOrThrow(Long requester, Long requestId) {
        return requestRepository.findRequestByRequesterIdAndId(requester, requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request id = %s not found", requestId)));
    }

    private void validatedRequest(Event event, List<Request> requests, RequestStatus status) {
        for (Request request : requests) {
            int participantCount = requestRepository.countRequestByStatusAndEventId(CONFIRMED, event.getId());
            if (!request.getStatus().equals(PENDING)) {
                throw new RequestEventException("State should be PENDING");
            }
            if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= participantCount) {
                throw new RequestEventException("There are no seats but you are holding on");
            }
            if (status.equals(REJECTED)) {
                request.setStatus(REJECTED);
            }
            if (status.equals(CONFIRMED)) {
                request.setStatus(CONFIRMED);
            }
        }
    }
}
