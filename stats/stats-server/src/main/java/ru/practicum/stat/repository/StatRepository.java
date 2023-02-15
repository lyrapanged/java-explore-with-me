package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "SELECT new ru.practicum.stat.model.ViewStats(" +
            "st.app, st.uri, COUNT(st.ip)) " +
            "FROM EndpointHit st " +
            "WHERE st.timestamp BETWEEN :start AND :end " +
            "AND st.uri in ( :uris ) " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC")
    List<ViewStats> get(@Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.stat.model.ViewStats(" +
            "st.app , st.uri, COUNT(DISTINCT st.ip)) " +
            "FROM EndpointHit st " +
            "WHERE st.timestamp BETWEEN :start AND :end " +
            "AND st.uri IN ( :uris ) " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(DISTINCT st.ip) DESC")
    List<ViewStats> getUnique(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end,
                              @Param("uris") List<String> uris);
}
