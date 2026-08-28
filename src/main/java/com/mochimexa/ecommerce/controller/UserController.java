package com.mochimexa.ecommerce.controller;

import com.mochimexa.ecommerce.DTO.UserRequestDTO;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.DTO.UserResponseDTO;
import com.mochimexa.ecommerce.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserRequestDTO dto) {
        return userService.toResponse(userService.create(dto));
    }

    @PutMapping("/{id}")
    public User update(
            @PathVariable Integer id,
            @Valid @RequestBody UserRequestDTO dto
    ) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        userService.deleteById(id);
    }
}
