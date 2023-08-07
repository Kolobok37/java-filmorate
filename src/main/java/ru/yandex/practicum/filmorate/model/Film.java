package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int filmId;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;
    private long duration;
    private final Set<Integer> userIdLikedFilm = new HashSet<>();

    public Film(int filmId, String name, String description, LocalDate releaseDate, long duration) {
        this.filmId = filmId;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(int idUser){
        userIdLikedFilm.add(idUser);
    }

    public void deleteLike(int idUser){
        userIdLikedFilm.remove(idUser);
    }

    public Integer getNumberLike(){
        return userIdLikedFilm.size();
    }
}
