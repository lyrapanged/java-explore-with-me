package ru.practicum.explore.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
