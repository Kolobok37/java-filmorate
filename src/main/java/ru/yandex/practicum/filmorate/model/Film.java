package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)

    private LocalDate releaseDate;
    private Duration duration;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public Film(int id, String name, String description, String releaseDate, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate, formatter);
        this.duration = duration;
    }
}
