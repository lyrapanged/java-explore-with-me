package ru.practicum.explore.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Location {
    private Float lat;
    private Float lon;
}
