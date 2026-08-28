package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.StoreSettingsDTO;
import com.mochimexa.ecommerce.model.StoreSettings;
import com.mochimexa.ecommerce.repository.StoreSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StoreSettingsService {
    private static final int ID = 1;
    private final StoreSettingsRepository repository;

    public StoreSettingsService(StoreSettingsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public StoreSettings getEntity() {
        return repository.findById(ID).orElseGet(() -> repository.save(defaults()));
    }

    @Transactional(readOnly = true)
    public StoreSettingsDTO get() {
        return toDto(repository.findById(ID).orElseGet(this::defaults));
    }

    @Transactional
    public StoreSettingsDTO update(StoreSettingsDTO dto) {
        StoreSettings settings = getEntity();
        settings.setNombreAdmin(dto.getNombreAdmin().trim());
        settings.setCorreoAdmin(dto.getCorreoAdmin().trim().toLowerCase());
        settings.setEnvioCdmx(dto.getEnvioCdmx());
        settings.setEnvioInterior(dto.getEnvioInterior());
        settings.setPagoTarjeta(Boolean.TRUE.equals(dto.getMetodosPago().get("tarjeta")));
        settings.setPagoPaypal(Boolean.TRUE.equals(dto.getMetodosPago().get("paypal")));
        settings.setPagoSpei(Boolean.TRUE.equals(dto.getMetodosPago().get("spei")));
        settings.setPagoOxxo(Boolean.TRUE.equals(dto.getMetodosPago().get("oxxo")));
        settings.setNotificarPedidos(Boolean.TRUE.equals(dto.getNotificaciones().get("nuevosPedidos")));
        settings.setNotificarStock(Boolean.TRUE.equals(dto.getNotificaciones().get("stockBajo")));
        settings.setResumenSemanal(Boolean.TRUE.equals(dto.getNotificaciones().get("resumenSemanal")));
        return toDto(repository.save(settings));
    }

    public boolean paymentEnabled(StoreSettings settings, String method) {
        return switch (method.toLowerCase()) {
            case "tarjeta" -> Boolean.TRUE.equals(settings.getPagoTarjeta());
            case "paypal" -> Boolean.TRUE.equals(settings.getPagoPaypal());
            case "spei" -> Boolean.TRUE.equals(settings.getPagoSpei());
            case "oxxo" -> Boolean.TRUE.equals(settings.getPagoOxxo());
            default -> false;
        };
    }

    private StoreSettings defaults() {
        StoreSettings settings = new StoreSettings();
        settings.setId(ID);
        settings.setNombreAdmin("Administración MochiMexa");
        settings.setCorreoAdmin("admin@mochimexa.com");
        settings.setEnvioCdmx(new BigDecimal("80.00"));
        settings.setEnvioInterior(new BigDecimal("150.00"));
        settings.setPagoTarjeta(true);
        settings.setPagoPaypal(true);
        settings.setPagoSpei(true);
        settings.setPagoOxxo(false);
        settings.setNotificarPedidos(true);
        settings.setNotificarStock(true);
        settings.setResumenSemanal(false);
        return settings;
    }

    private StoreSettingsDTO toDto(StoreSettings settings) {
        Map<String, Boolean> payments = new LinkedHashMap<>();
        payments.put("tarjeta", settings.getPagoTarjeta());
        payments.put("paypal", settings.getPagoPaypal());
        payments.put("spei", settings.getPagoSpei());
        payments.put("oxxo", settings.getPagoOxxo());
        Map<String, Boolean> notifications = new LinkedHashMap<>();
        notifications.put("nuevosPedidos", settings.getNotificarPedidos());
        notifications.put("stockBajo", settings.getNotificarStock());
        notifications.put("resumenSemanal", settings.getResumenSemanal());
        return new StoreSettingsDTO(settings.getNombreAdmin(), settings.getCorreoAdmin(), settings.getEnvioCdmx(),
                settings.getEnvioInterior(), payments, notifications);
    }
}
