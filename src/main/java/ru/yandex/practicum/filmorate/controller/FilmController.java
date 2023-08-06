package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос.");
        return films.values().stream().collect(Collectors.toList());
    }

    @PostMapping(value = "/films", consumes = {"application/json"})
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        if (!validationFilm(film)) {
                throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошабка ввода данных фильма");
        }
        films.put(film.getId(), film);
        log.info("Добавлен фильм \"" + film.getName() + "\"");

        return film;
    }


    @PutMapping(value = "/films", consumes = {"application/json"})
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка ввода данных фильма");
        }
        if (!validationFilm(film)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка ввода данных фильма");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм \"" + film.getName() + "\"");
        return film;
    }

    private boolean validationFilm(Film film) {
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
            log.warn("Задана отрицательная продолжительность фильма.");
            return false;
        }
        return true;
    }

    public void removeAll() {
        films.clear();
    }
}
