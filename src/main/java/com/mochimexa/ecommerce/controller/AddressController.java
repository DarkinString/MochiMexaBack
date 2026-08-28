package com.mochimexa.ecommerce.controller;

import com.mochimexa.ecommerce.DTO.AddressRequestDTO;
import com.mochimexa.ecommerce.DTO.AddressResponseDTO;
import com.mochimexa.ecommerce.service.AddressService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/user/{userId}")
    public List<AddressResponseDTO> getByUserId(
            @PathVariable Integer userId
    ) {
        return addressService.findByUserId(userId);
    }

    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDTO create(
            @PathVariable Integer userId,
            @Valid @RequestBody AddressRequestDTO dto
    ) {
        return addressService.create(userId, dto);
    }

    @PutMapping("/{id}")
    public AddressResponseDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody AddressRequestDTO dto
    ) {
        return addressService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        addressService.deleteById(id);
    }
}