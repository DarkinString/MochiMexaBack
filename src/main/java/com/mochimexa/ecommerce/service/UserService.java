package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.UserRequestDTO;
import com.mochimexa.ecommerce.model.Contrasenia;
import com.mochimexa.ecommerce.model.Rol;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.ContraseniaRepository;
import com.mochimexa.ecommerce.repository.RolRepository;
import com.mochimexa.ecommerce.repository.UserRepository;

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
    private final ContraseniaRepository contraseniaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RolRepository rolRepository,
            ContraseniaRepository contraseniaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.contraseniaRepository = contraseniaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    @Transactional
    public User create(UserRequestDTO dto) {

        if (userRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo ya está registrado"
            );
        }

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado"
                ));

        User usuario = new User();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);
        usuario.setRol(rol);

        User usuarioGuardado = userRepository.save(usuario);

        Contrasenia contrasenia = new Contrasenia();
        contrasenia.setPasswordHash(
                passwordEncoder.encode(dto.getContrasenia())
        );
        contrasenia.setUsuario(usuarioGuardado);

        contraseniaRepository.save(contrasenia);

        return usuarioGuardado;
    }

    @Transactional
    public User update(Integer id, UserRequestDTO dto) {

        User usuario = findById(id);

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado"
                ));

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(rol);

        return userRepository.save(usuario);
    }

    @Transactional
    public void deleteById(Integer id) {

        User usuario = findById(id);

        userRepository.delete(usuario);
    }
}