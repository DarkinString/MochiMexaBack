package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.AddressRequestDTO;
import com.mochimexa.ecommerce.DTO.AddressResponseDTO;
import com.mochimexa.ecommerce.model.Address;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.AddressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(AddressRepository addressRepository, UserService userService) {
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
    public AddressResponseDTO findById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada"));
        return convertToDTO(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> findByUserId(Long userId) {
        return addressRepository.findByUsuarioIdUsuario(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponseDTO create(Long userId, AddressRequestDTO dto) {
        User usuario = userService.findById(userId);

        Address address = new Address();
        address.setCalle(dto.getCalle());
        address.setNumero(dto.getNumero());
        address.setColonia(dto.getColonia());
        address.setCodigoPostal(dto.getCodigoPostal());
        address.setCiudad(dto.getCiudad());
        address.setEstado(dto.getEstado());
        address.setReferencia(dto.getReferencia());
        address.setUsuario(usuario);

        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Transactional
    public AddressResponseDTO update(Long id, AddressRequestDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada"));

        address.setCalle(dto.getCalle());
        address.setNumero(dto.getNumero());
        address.setColonia(dto.getColonia());
        address.setCodigoPostal(dto.getCodigoPostal());
        address.setCiudad(dto.getCiudad());
        address.setEstado(dto.getEstado());
        address.setReferencia(dto.getReferencia());

        Address updatedAddress = addressRepository.save(address);
        return convertToDTO(updatedAddress);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada");
        }
        addressRepository.deleteById(id);
    }

    // Método mapeador privado
    private AddressResponseDTO convertToDTO(Address address) {
        return new AddressResponseDTO(
                address.getIdDireccion(),
                address.getCalle(),
                address.getNumero(),
                address.getColonia(),
                address.getCodigoPostal(),
                address.getCiudad(),
                address.getEstado(),
                address.getReferencia(),
                address.getUsuario() != null ? address.getUsuario().getIdUsuario() : null
        );
    }

}
