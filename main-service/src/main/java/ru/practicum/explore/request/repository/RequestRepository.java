package ru.practicum.explore.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.request.model.Request;

import java.util.List;

import static ru.practicum.explore.request.model.Request.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByStatusAndIdIn(RequestStatus status, List<Long> ids);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByStatusAndAndEventId(RequestStatus status, Long id);

    List<Request> findAllByEventIdAndIdIn(Long eventId, List<Long> ids);
}
