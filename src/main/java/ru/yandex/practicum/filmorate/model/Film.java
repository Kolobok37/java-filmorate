package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;
    private long duration;
    private int rate;
    private Set<Integer> userIdLikedFilm = new HashSet<>();
    @JsonDeserialize(as = HashSet.class)
    private Set<Genre> genres;
    private Mpa mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, int rate, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
        genres = new HashSet<>();
    }

    public void addLike(int idUser) {
        userIdLikedFilm.add(idUser);
        rate++;
    }

    public void deleteLike(int idUser) {
        userIdLikedFilm.remove(idUser);
        rate--;
    }

    public Integer getNumberLike() {
        return rate;
    }

    public void setMpa(int mpaId) {
        mpa.setId(mpaId);
    }
}
