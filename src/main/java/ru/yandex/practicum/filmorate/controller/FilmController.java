package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос.");
        return filmService.getFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        return filmService.getFilm(filmId);
    }

    @GetMapping("/films/popular?count={count}")
    public List<Film> getPopularFilm(@RequestParam(defaultValue = "10") int count){
        return filmService.getPopularFilm(count);
    }

    @PostMapping(value = "/films", consumes = {"application/json"})
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }


    @PutMapping(value = "/films", consumes = {"application/json"})
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.addLike(filmId, userId);
        return filmService.getFilm(filmId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable int filmId, @PathVariable int userId){
        filmService.deleteLike(filmId,userId);
        return filmService.getFilm(filmId);
    }

    @ExceptionHandler
    public Map<String, String> handleNegativeCount(final ValidationException e) {
        return Map.of("error", e.getMessage());
    }
}
