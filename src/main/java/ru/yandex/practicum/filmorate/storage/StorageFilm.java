package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

public interface StorageFilm extends Storage<Film> {
    Logger log = LoggerFactory.getLogger(UserController.class);

    List<Film> getPopularFilm(int count);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Genre> getGenres();

    List<Mpa> getAllMpa();

    default boolean validationFilm(Film film) {
        if (film.getName() == null || film.getDescription() == null || film.getReleaseDate() == null) {
            log.warn("Данные фильма заполнены неполностью.");
            return false;
        }
        if (film.getName().isBlank()) {
            log.warn("Задано пустое название фильма.");
            return false;
        }
        if (film.getDescription().length() > 200) {
            log.warn("Задано слишком длинное описание фильма.");
            return false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Задана недоступная дата выхода фильма.");
            return false;
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма заданна неверно.");
            return false;
        }
        return true;
    }
}
