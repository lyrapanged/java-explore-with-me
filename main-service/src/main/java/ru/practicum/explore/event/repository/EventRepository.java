package ru.practicum.explore.event.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explore.event.model.Event;

import java.util.List;

import static ru.practicum.explore.event.model.Event.State;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Page<Event> findAll(Predicate value, Pageable pageable);

    List<Event> getAllByInitiatorId(Long userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> events);

    Event findByIdAndState(Long id, State published);
}
