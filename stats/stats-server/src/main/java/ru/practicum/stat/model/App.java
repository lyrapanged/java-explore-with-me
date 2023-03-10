package ru.practicum.stat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "app")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class App {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
}
