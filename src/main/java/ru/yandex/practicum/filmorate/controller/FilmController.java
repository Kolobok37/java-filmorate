package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

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
        log.info("Запрошен список фильмов.");

        return filmService.getFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        log.info("Запрошен фильм {}", filmId);
        return filmService.getFilm(filmId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilm(@RequestParam(required = false, defaultValue = "10") String count) {
        log.info("Запрошено {} популярных фильмов", count);
        return filmService.getPopularFilm(Integer.parseInt(count));
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        log.info("Запрошен список жанров.");
        return filmService.getGenres();
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenre(@PathVariable int genreId) {
        log.info("Запрошен жанр {}", genreId);
        Optional<Genre> genre = filmService.getGenres().stream().filter(genre1 -> genre1.getId() == genreId).findFirst();
        if(genre.isEmpty()){
            throw new NotFoundException("Жанр не найден.");
        }
        return genre.get();
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        log.info("Запрошен список возврастных рейтингов.");
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa getMpa(@PathVariable int mpaId) {
        log.info("Запрошен возврастной рейтинг", mpaId);
        Optional<Mpa> mpa = filmService.getAllMpa().stream().filter(mpa1 -> mpa1.getId() == mpaId).findFirst();
        if(mpa.isEmpty()){
            throw new NotFoundException("Возврастной рейтинг не найден.");
        }
        return mpa.get();
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
    public Film deleteLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.deleteLike(filmId, userId);
        return filmService.getFilm(filmId);
    }
}
