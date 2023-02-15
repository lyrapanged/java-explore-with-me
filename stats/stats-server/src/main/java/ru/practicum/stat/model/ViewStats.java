package ru.practicum.stat.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stat")
public class ViewStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String app;
    private String uri;
    private Long hits;

    public ViewStats(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;

    }
}
