package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private final Set<Integer> userIdLikedFilm = new HashSet<>();
    private int rate;

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, int rate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
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
}
