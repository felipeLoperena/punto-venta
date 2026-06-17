package com.truman.punto_venta.usuario.service;

import com.truman.punto_venta.usuario.domain.Rol;
import com.truman.punto_venta.usuario.domain.Usuario;
import com.truman.punto_venta.usuario.repo.UsuarioRepository;
import com.truman.punto_venta.usuario.web.UsuarioForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class UsuarioService {

    private final UsuarioRepository  usuarioRepository;
    private final PasswordEncoder    passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    // ── Listar ───────────────────────────────────────────────────

    public Page<Usuario> listar(String nombre, Pageable pageable) {
        if (nombre != null && !nombre.isBlank()) {
            return usuarioRepository.findByNombreContainingIgnoreCase(nombre.trim(), pageable);
        }
        return usuarioRepository.findAll(pageable);
    }

    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));
    }

    // ── Crear ────────────────────────────────────────────────────

    @Transactional
    public Usuario crear(UsuarioForm form) {
        if (usuarioRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email.");
        }
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria al crear un usuario.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(form.getNombre().trim());
        usuario.setEmail(form.getEmail().trim().toLowerCase());
        usuario.setPassword(passwordEncoder.encode(form.getPassword()));
        usuario.setRol(form.getRol());
        usuario.setActivo(form.isActivo());

        return usuarioRepository.save(usuario);
    }

    // ── Editar ───────────────────────────────────────────────────

    @Transactional
    public Usuario editar(Long id, UsuarioForm form) {
        Usuario usuario = obtener(id);

        // Verificar email duplicado en otro usuario
        usuarioRepository.findByEmail(form.getEmail().trim().toLowerCase())
                .ifPresent(otro -> {
                    if (!otro.getId().equals(id)) {
                        throw new IllegalArgumentException("Ese email ya está en uso por otro usuario.");
                    }
                });

        usuario.setNombre(form.getNombre().trim());
        usuario.setEmail(form.getEmail().trim().toLowerCase());
        usuario.setRol(form.getRol());
        usuario.setActivo(form.isActivo());

        // Solo actualizar contraseña si se proporcionó una nueva
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    // ── Activar / Desactivar ─────────────────────────────────────

    @Transactional
    public void toggleActivo(Long id) {
        Usuario usuario = obtener(id);
        if (usuario.isActivo()
                && usuario.getRol() == Rol.ADMIN
                && usuarioRepository.countByRolAndActivoTrue(Rol.ADMIN) <= 1) {
            throw new IllegalArgumentException(
                    "No se puede desactivar al único administrador activo del sistema.");
        }
        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
    }
}