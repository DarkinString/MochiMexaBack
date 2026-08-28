package com.mochimexa.ecommerce.controller;

import com.mochimexa.ecommerce.DTO.StoreSettingsDTO;
import com.mochimexa.ecommerce.service.StoreSettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class SettingsController {
    private final StoreSettingsService service;

    public SettingsController(StoreSettingsService service) {
        this.service = service;
    }

    @GetMapping("/api/settings")
    public StoreSettingsDTO get() {
        return service.get();
    }

    @PutMapping("/api/admin/settings")
    public StoreSettingsDTO update(@Valid @RequestBody StoreSettingsDTO dto) {
        return service.update(dto);
    }
}
