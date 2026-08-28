package com.mochimexa.ecommerce.security;

import com.mochimexa.ecommerce.model.Contrasenia;
import com.mochimexa.ecommerce.model.User;
import com.mochimexa.ecommerce.repository.ContraseniaRepository;
import com.mochimexa.ecommerce.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ContraseniaRepository contraseniaRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            ContraseniaRepository contraseniaRepository
    ) {
        this.userRepository = userRepository;
        this.contraseniaRepository = contraseniaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        User usuario = userRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado"
                ));

        Contrasenia contrasenia = contraseniaRepository
                .findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Contraseña no encontrada para el usuario"
                ));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getCorreo())
                .password(contrasenia.getPasswordHash())
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .roles(usuario.getRol().getRolAsignado().toUpperCase())
                .build();
    }
}
