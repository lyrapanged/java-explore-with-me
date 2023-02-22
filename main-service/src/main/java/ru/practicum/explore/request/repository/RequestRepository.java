package ru.practicum.explore.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.request.model.Request;

import java.util.List;
import java.util.Optional;

import static ru.practicum.explore.request.model.Request.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByStatusAndIdIn(RequestStatus status, List<Long> ids);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByStatusAndEventIdIn(RequestStatus status, List<Long> ids);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByStatusAndAndEventId(RequestStatus status, Long id);

    List<Request> findAllByEventIdAndIdIn(Long eventId, List<Long> ids);

    int countRequestByStatusAndEventId(RequestStatus status, Long eventId);

    Optional<Request> findRequestByRequesterIdAndId(Long requester, Long requestId);
}
