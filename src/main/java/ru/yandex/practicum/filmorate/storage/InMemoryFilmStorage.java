package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @Override
    public Film getFilm(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        addingFilmDate(film);
        if (!validationFilm(film)) {
            throw new ValidationException("Ошабка ввода данных фильма");
        }
        films.put(film.getId(), film);
        log.info("Добавлен фильм \"" + film.getName() + "\"");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        addingFilmDate(film);
        if (!validationFilm(film)) {
            throw new ValidationException("Ошибка ввода данных фильма");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм \"" + film.getName() + "\"");
        return film;
    }

    @Override
    public boolean checkExistenceFilm(int filmId) {
        return films.containsKey(filmId);
    }

    private void addingFilmDate(Film film) {
        if (film.getId() == 0) {
            if (films.size() == 0) {
                film.setId(1);
            } else {
                int a = films.keySet().stream().max((id1, id2) -> id1 - id2).get();
                film.setId(++a);
            }
        }
    }

    private boolean validationFilm(Film film) {
        if (film.getName() == null || film.getDescription() == null || film.getReleaseDate() == null) {
            log.warn("Данные фильма заполнены неполностью.");
            return false;
        }
        if (film.getName() == null || film.getName().isBlank()) {
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
