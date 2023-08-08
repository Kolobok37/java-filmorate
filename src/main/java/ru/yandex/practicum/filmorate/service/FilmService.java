package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage filmStorage;

    private final InMemoryUserStorage userStorage;

    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(int filmId) {
        checkExistenceFilm(filmId);
        return filmStorage.getFilm(filmId);
    }

    public List<Film> getPopularFilm(int count) {
        return filmStorage.getFilms().stream().sorted(Comparator.comparing(Film::getNumberLike).reversed()).
                limit(count).collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        checkExistenceFilm(film.getId());
        return filmStorage.updateFilm(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        checkExistenceFilm(filmId);
        userStorage.checkExistenceUser(userId);
        filmStorage.getFilm(filmId).addLike(userId);
        log.info("Пользователь %d поставил лайк фильму %d",userId,filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        checkExistenceFilm(filmId);
        checkExistenceUser(userId);
        filmStorage.getFilm(filmId).deleteLike(userId);
        log.info("Пользователь %d удалил лайк фильму %d",userId,filmId);
    }

    private void checkExistenceFilm(int filmId) {
        if (!filmStorage.checkExistenceFilm(filmId)) {
            log.warn("Ошибка: фильм %d не найден",filmId);
            throw new NotFoundException("Фильм не найден.");
        }
    }

    private void checkExistenceUser(int id){
        if(!userStorage.checkExistenceUser(id)){
            log.warn("Ошибка: пользователь %d не найден", id);
            throw new NotFoundException("Пользователь не найден.");
        }
    }
}
