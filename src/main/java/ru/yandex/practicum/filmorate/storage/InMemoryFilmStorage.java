package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component

public class InMemoryFilmStorage implements StorageFilm {

    private HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film searchById(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Film create(Film film) {
        addingFilmId(film);
        if (!validationFilm(film)) {
            throw new ValidationException("Ошабка ввода данных фильма");
        }
        films.put(film.getId(), film);
        log.info("Добавлен фильм \"" + film.getName() + "\"");
        return film;
    }

    @Override
    public Film update(Film film) {
        addingFilmId(film);
        if (!validationFilm(film)) {
            throw new ValidationException("Ошибка ввода данных фильма");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм \"" + film.getName() + "\"");
        return film;
    }

    @Override
    public boolean checkExistence(int filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        return getAll().stream()
                .sorted(Comparator.comparing(Film::getNumberLike).reversed())
                .limit(count).collect(Collectors.toList());
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        searchById(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        searchById(filmId).deleteLike(userId);
    }

    @Override
    public List<Genre> getGenres() {
        return null;
    }

    @Override
    public List<Mpa> getAllMpa() {
        return null;
    }

    public void addingFilmId(Film film) {
        if (film.getId() == 0) {
            if (films.size() == 0) {
                film.setId(1);
            } else {
                int id = films.keySet().stream().max((id1, id2) -> id1 - id2).get();
                film.setId(++id);
            }
        }
    }
}
