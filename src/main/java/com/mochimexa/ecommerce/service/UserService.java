package com.mochimexa.ecommerce.service;


import com.mochimexa.ecommerce.DTO.UserRequestDTO;
import com.mochimexa.ecommerce.model.Rol;
import com.mochimexa.ecommerce.repository.RolRepository;
import com.mochimexa.ecommerce.repository.UserRepository;
import com.mochimexa.ecommerce.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    //CONSTRUCTOR
    public UserService(UserRepository userRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //RECUPERA UNA LISTA DE LOS USUARIOS REGISTRADOS
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    //BUSCA UN USUARIO POR ID
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    //CREA UN USUARIO. VERIFICA QUE EL CORREO NO ESTE DUPLICADO
    @Transactional
    public User create(UserRequestDTO dto) {
        if (userRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo ya está registrado");
        }

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        User usuario = new User();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasenia(passwordEncoder.encode(dto.getContrasenia()));
        usuario.setTelefono(dto.getTelefono());
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);
        usuario.setRol(rol);

        return userRepository.save(usuario);
    }

    //MODIFICA UN USUARIO USANDO SU ID. CAMBIA NOMBRE, APELLIDO, TELEFONO Y ROL
    @Transactional
    public User update(Long id, UserRequestDTO dto) {
        User usuario = findById(id);

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(rol);

        return userRepository.save(usuario);

    }

    public void deleteById(Long id) {

    }
}
