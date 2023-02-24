package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stat.model.App;

import java.util.Optional;

public interface AppRepository extends JpaRepository<App, Long> {

    Optional<App> findByName(String name);
}
