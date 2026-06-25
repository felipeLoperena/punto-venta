package com.truman.punto_venta.proveedor.webui;

import com.truman.punto_venta.proveedor.domain.Proveedor;
import com.truman.punto_venta.proveedor.service.ProveedorService;
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
@RequestMapping("/proveedores")
public class ProveedorPageController {

    private final ProveedorService service;

    public ProveedorPageController(ProveedorService service) {
        this.service = service;
    }

    // LISTA PAGINADA + BÚSQUEDA
    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String q,
                         @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable,
                         Model model) {
        Page<Proveedor> page = service.listar(q.isBlank() ? null : q, pageable);
        model.addAttribute("page", page);
        model.addAttribute("q", q);
        model.addAttribute("activeModule", "proveedores");
        return "proveedores/list";
    }

    // FORM CREAR
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("modo", "crear");
        model.addAttribute("activeModule", "proveedores");
        return "proveedores/form";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute("proveedor") @Valid Proveedor proveedor,
                        BindingResult binding,
                        Model model,
                        RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("modo", "crear");
            model.addAttribute("activeModule", "proveedores");
            return "proveedores/form";
        }
        service.crear(proveedor);
        ra.addFlashAttribute("ok", "Proveedor creado correctamente.");
        return "redirect:/proveedores";
    }

    // FORM EDITAR
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("proveedor", service.obtener(id));
        model.addAttribute("modo", "editar");
        model.addAttribute("activeModule", "proveedores");
        return "proveedores/form";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         @ModelAttribute("proveedor") @Valid Proveedor proveedor,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("modo", "editar");
            model.addAttribute("activeModule", "proveedores");
            return "proveedores/form";
        }
        service.actualizar(id, proveedor);
        ra.addFlashAttribute("ok", "Proveedor actualizado correctamente.");
        return "redirect:/proveedores";
    }

    // ELIMINAR
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        service.eliminar(id);
        ra.addFlashAttribute("ok", "Proveedor eliminado.");
        return "redirect:/proveedores";
    }
}
