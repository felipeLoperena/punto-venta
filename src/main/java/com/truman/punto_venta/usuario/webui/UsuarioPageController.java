package com.truman.punto_venta.usuario.webui;

import com.truman.punto_venta.usuario.domain.Rol;
import com.truman.punto_venta.usuario.domain.Usuario;
import com.truman.punto_venta.usuario.service.UsuarioService;
import com.truman.punto_venta.usuario.web.UsuarioForm;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioPageController {

    private final UsuarioService usuarioService;

    public UsuarioPageController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ── Login ─────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    // ── Listado ───────────────────────────────────────────────────

    @GetMapping("/usuarios")
    public String listar(
            @RequestParam(defaultValue = "") String nombre,
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {

        Page<Usuario> page = usuarioService.listar(nombre.isBlank() ? null : nombre, pageable);
        model.addAttribute("page",         page);
        model.addAttribute("nombre",       nombre);
        model.addAttribute("activeModule", "usuarios");
        return "usuarios/list";
    }

    // ── Crear ─────────────────────────────────────────────────────

    @GetMapping("/usuarios/crear")
    public String crearForm(Model model) {
        model.addAttribute("usuarioForm",  new UsuarioForm());
        model.addAttribute("roles",        Rol.values());
        model.addAttribute("modo",         "crear");
        model.addAttribute("activeModule", "usuarios");
        return "usuarios/form";
    }

    @PostMapping("/usuarios/crear")
    public String crear(@Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes flash) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            result.rejectValue("password", "required", "La contraseña es obligatoria");
        } else if (form.getPassword().length() < 6) {
            result.rejectValue("password", "size", "Mínimo 6 caracteres");
        }
        if (result.hasErrors()) {
            model.addAttribute("roles",        Rol.values());
            model.addAttribute("modo",         "crear");
            model.addAttribute("activeModule", "usuarios");
            return "usuarios/form";
        }
        try {
            usuarioService.crear(form);
            flash.addFlashAttribute("ok", "Usuario creado correctamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error",        e.getMessage());
            model.addAttribute("roles",        Rol.values());
            model.addAttribute("modo",         "crear");
            model.addAttribute("activeModule", "usuarios");
            return "usuarios/form";
        }
        return "redirect:/usuarios";
    }

    // ── Editar ────────────────────────────────────────────────────

    @GetMapping("/usuarios/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        Usuario u = usuarioService.obtener(id);

        UsuarioForm form = new UsuarioForm();
        form.setId(u.getId());
        form.setNombre(u.getNombre());
        form.setEmail(u.getEmail());
        form.setRol(u.getRol());
        form.setActivo(u.isActivo());

        model.addAttribute("usuarioForm",  form);
        model.addAttribute("roles",        Rol.values());
        model.addAttribute("modo",         "editar");
        model.addAttribute("activeModule", "usuarios");
        return "usuarios/form";
    }

    @PostMapping("/usuarios/{id}/editar")
    public String editar(@PathVariable Long id,
                         @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes flash) {
        String pwd = form.getPassword();
        if (pwd != null && !pwd.isBlank() && pwd.length() < 6) {
            result.rejectValue("password", "size", "Mínimo 6 caracteres");
        }
        if (result.hasErrors()) {
            model.addAttribute("roles",        Rol.values());
            model.addAttribute("modo",         "editar");
            model.addAttribute("activeModule", "usuarios");
            return "usuarios/form";
        }
        try {
            usuarioService.editar(id, form);
            flash.addFlashAttribute("ok", "Usuario actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error",        e.getMessage());
            model.addAttribute("roles",        Rol.values());
            model.addAttribute("modo",         "editar");
            model.addAttribute("activeModule", "usuarios");
            return "usuarios/form";
        }
        return "redirect:/usuarios";
    }

    // ── Toggle activo ─────────────────────────────────────────────

    @PostMapping("/usuarios/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.toggleActivo(id);
            flash.addFlashAttribute("ok", "Estado del usuario actualizado.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}