package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @PostMapping
    public Director saveDirector(@RequestBody @Valid Director director) {
        return directorService.saveDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        return directorService.updateDirector(director);
    }

    @GetMapping("/{id}")
    public Optional<Director> getDirectorById(@PathVariable Long id) {
        return directorService.getDirectorById(id);
    }

    @DeleteMapping("/{id}")
    public Optional<Director> deleteDirectorById(@PathVariable Long id) {
        return directorService.deleteDirectorById(id);
    }
}
