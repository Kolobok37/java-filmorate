package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;

@Data
@JsonComponent
public class Mpa {

    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
}
