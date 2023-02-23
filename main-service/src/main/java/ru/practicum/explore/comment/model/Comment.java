package ru.practicum.explore.comment.model;

import lombok.*;
import ru.practicum.explore.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    private long eventId;
    private String text;
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        PUBLISHED,
        CANCELED
    }
}
