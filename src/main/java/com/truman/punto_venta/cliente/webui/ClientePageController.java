package com.truman.punto_venta.cliente.webui;

import com.truman.punto_venta.cliente.domain.Cliente;
import com.truman.punto_venta.cliente.service.ClienteService;
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
@RequestMapping("/clientes")
public class ClientePageController {

    private final ClienteService service;

    public ClientePageController(ClienteService service) {
        this.service = service;
    }

    // LISTA PAGINADA + BÚSQUEDA
    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String q,
                         @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable,
                         Model model) {
        Page<Cliente> page = service.listar(q.isBlank() ? null : q, pageable);
        model.addAttribute("page", page);
        model.addAttribute("q", q);
        model.addAttribute("activeModule", "clientes");
        return "clientes/list";
    }

    // FORM CREAR
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modo", "crear");
        model.addAttribute("activeModule", "clientes");
        return "clientes/form";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute("cliente") @Valid Cliente cliente,
                        BindingResult binding,
                        Model model,
                        RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("modo", "crear");
            model.addAttribute("activeModule", "clientes");
            return "clientes/form";
        }
        service.crear(cliente);
        ra.addFlashAttribute("ok", "Cliente creado correctamente.");
        return "redirect:/clientes";
    }

    // FORM EDITAR
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", service.obtener(id));
        model.addAttribute("modo", "editar");
        model.addAttribute("activeModule", "clientes");
        return "clientes/form";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         @ModelAttribute("cliente") @Valid Cliente cliente,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("modo", "editar");
            model.addAttribute("activeModule", "clientes");
            return "clientes/form";
        }
        service.actualizar(id, cliente);
        ra.addFlashAttribute("ok", "Cliente actualizado correctamente.");
        return "redirect:/clientes";
    }

    // ELIMINAR
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        service.eliminar(id);
        ra.addFlashAttribute("ok", "Cliente eliminado.");
        return "redirect:/clientes";
    }
}
