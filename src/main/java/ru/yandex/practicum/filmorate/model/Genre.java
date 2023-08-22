package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Genre {
    @JsonProperty("id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        switch (id) {
            case (1):
                return "Комедия";
            case (2):
                return "Драма";
            case (3):
                return "Мультфильм";
            case (4):
                return "Триллер";
            case (5):
                return "Документальный";
            default:
                return "Боевик";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


