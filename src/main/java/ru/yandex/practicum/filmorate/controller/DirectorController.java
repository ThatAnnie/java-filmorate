package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director saveDirector(@RequestBody @Valid Director director) {
        return directorService.saveDirector(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@RequestBody @Valid Director director) {
        return directorService.updateDirector(director);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@PathVariable Long id) {
        return directorService.getDirectorById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirectorById(@PathVariable Long id) {
        directorService.deleteDirectorById(id);
    }
}
