package com.mochimexa.ecommerce.service;

import com.mochimexa.ecommerce.DTO.UserRequestDTO;
import com.mochimexa.ecommerce.DTO.UpdateProfileRequestDTO;
import com.mochimexa.ecommerce.DTO.UserResponseDTO;
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

    @Transactional(readOnly = true)
    public User findByCorreo(String correo) {
        return userRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    @Transactional
    public User create(UserRequestDTO dto) {

        String correo = dto.getCorreo() == null ? "" : dto.getCorreo().trim().toLowerCase();
        if (correo.isBlank() || dto.getContrasenia() == null || dto.getContrasenia().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo y contraseña válida son obligatorios");
        }
        if (userRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo ya está registrado"
            );
        }

        // El registro público nunca decide privilegios desde el navegador.
        Rol rol = rolRepository.findByRolAsignadoIgnoreCase("CLIENTE")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "El rol CLIENTE no está configurado"
                ));

        User usuario = new User();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(correo);
        usuario.setTelefono(dto.getTelefono());
        usuario.setFoto(null);
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

        Rol rol = dto.getIdRol() == null ? usuario.getRol() : rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(rol);

        return userRepository.save(usuario);
    }

    @Transactional
    public User updateOwnProfile(Integer id, UpdateProfileRequestDTO dto) {
        User usuario = findById(id);
        String correo = dto.getCorreo().trim().toLowerCase();
        userRepository.findByCorreoIgnoreCase(correo)
                .filter(existing -> !existing.getIdUsuario().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo ya está registrado");
                });
        if (dto.getFoto() != null && dto.getFoto().length() > 2_800_000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La foto excede el tamaño permitido");
        }
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setCorreo(correo);
        usuario.setTelefono(dto.getTelefono() == null ? null : dto.getTelefono().trim());
        usuario.setFoto(dto.getFoto());
        return userRepository.save(usuario);
    }

    @Transactional
    public void changePassword(Integer id, String actual, String nueva) {
        Contrasenia contrasenia = contraseniaRepository.findByUsuarioIdUsuario(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contraseña no encontrada"));
        if (!passwordEncoder.matches(actual, contrasenia.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no coincide");
        }
        contrasenia.setPasswordHash(passwordEncoder.encode(nueva));
        contraseniaRepository.save(contrasenia);
    }

    public UserResponseDTO toResponse(User usuario) {
        return new UserResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getFoto(),
                usuario.getFechaRegistro(),
                usuario.getActivo(),
                usuario.getRol().getRolAsignado()
        );
    }

    @Transactional
    public void deleteById(Integer id) {

        User usuario = findById(id);

        userRepository.delete(usuario);
    }
}
