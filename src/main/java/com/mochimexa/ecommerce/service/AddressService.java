package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.AddressRequestDTO;
import com.mochimexa.ecommerce.DTO.AddressResponseDTO;
import com.mochimexa.ecommerce.model.Address;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.AddressRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(
            AddressRepository addressRepository,
            UserService userService
    ) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> findAll() {
        return addressRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressResponseDTO findById(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dirección no encontrada"
                ));

        return convertToDTO(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> findByUserId(Integer userId) {
        return addressRepository.findByUsuarioIdUsuario(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponseDTO create(
            Integer userId,
            AddressRequestDTO dto
    ) {
        User usuario = userService.findById(userId);

        Address address = new Address();
        address.setUsuario(usuario);
        List<Address> anteriores = addressRepository.findByUsuarioIdUsuario(userId);
        apply(address, dto);
        address.setPrincipal(Boolean.TRUE.equals(dto.getPrincipal()) || anteriores.isEmpty());
        if (Boolean.TRUE.equals(address.getPrincipal())) clearPrincipal(anteriores);

        Address savedAddress = addressRepository.save(address);

        return convertToDTO(savedAddress);
    }

    @Transactional
    public AddressResponseDTO update(
            Integer id,
            AddressRequestDTO dto
    ) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dirección no encontrada"
                ));

        apply(address, dto);
        if (Boolean.TRUE.equals(dto.getPrincipal())) {
            clearPrincipal(addressRepository.findByUsuarioIdUsuario(address.getUsuario().getIdUsuario()));
            address.setPrincipal(true);
        }

        Address updatedAddress = addressRepository.save(address);

        return convertToDTO(updatedAddress);
    }

    @Transactional
    public void deleteById(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada"));
        Integer userId = address.getUsuario().getIdUsuario();
        boolean eraPrincipal = Boolean.TRUE.equals(address.getPrincipal());
        addressRepository.delete(address);
        if (eraPrincipal) addressRepository.findByUsuarioIdUsuario(userId).stream().findFirst().ifPresent(siguiente -> {
            siguiente.setPrincipal(true);
            addressRepository.save(siguiente);
        });
    }

    @Transactional
    public AddressResponseDTO updateForUser(Integer userId, Integer id, AddressRequestDTO dto) {
        Address address = addressRepository.findByIdDireccionAndUsuarioIdUsuario(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada"));
        return update(address.getIdDireccion(), dto);
    }

    @Transactional
    public void deleteForUser(Integer userId, Integer id) {
        addressRepository.findByIdDireccionAndUsuarioIdUsuario(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada"));
        deleteById(id);
    }

    private void apply(Address address, AddressRequestDTO dto) {
        address.setAlias(dto.getAlias().trim());
        address.setCalle(dto.getCalle().trim());
        address.setNumero(dto.getNumero().trim());
        address.setColonia(dto.getColonia().trim());
        address.setCodigoPostal(dto.getCodigoPostal().trim());
        address.setCiudad(dto.getCiudad().trim());
        address.setEstado(dto.getEstado().trim());
        address.setReferencia(dto.getReferencia() == null ? null : dto.getReferencia().trim());
        if (address.getPrincipal() == null) address.setPrincipal(false);
    }

    private void clearPrincipal(List<Address> addresses) {
        addresses.forEach(item -> item.setPrincipal(false));
        addressRepository.saveAll(addresses);
    }

    private AddressResponseDTO convertToDTO(Address address) {
        return new AddressResponseDTO(
                address.getIdDireccion(),
                address.getAlias(),
                address.getCalle(),
                address.getNumero(),
                address.getColonia(),
                address.getCodigoPostal(),
                address.getCiudad(),
                address.getEstado(),
                address.getReferencia(),
                address.getPrincipal(),
                address.getUsuario() != null
                        ? address.getUsuario().getIdUsuario()
                        : null
        );
    }
}
