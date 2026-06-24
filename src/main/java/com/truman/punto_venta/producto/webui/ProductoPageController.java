package com.truman.punto_venta.producto.webui;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.truman.punto_venta.producto.domain.Producto;
import com.truman.punto_venta.producto.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ProductoPageController {

    private final ProductoService service;

    public ProductoPageController(ProductoService service) {
        this.service = service;
    }

    // LISTA PAGINADA + FILTRO
    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String nombre,
                         @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable,
                         Model model) {
        Page<Producto> page = service.listar(nombre.isBlank() ? null : nombre, pageable);
        model.addAttribute("page", page);
        model.addAttribute("nombre", nombre);           // para mantener el filtro en el input
        model.addAttribute("sort", pageable.getSort()); // por si quieres pintarlo
        return "productos/list";
    }

    // FORM CREAR
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("modo", "crear");
        model.addAttribute("categorias", service.categorias());
        return "productos/form";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute("producto") @Valid Producto producto,
                        BindingResult binding,
                        Model model,
                        RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("modo", "crear");
            model.addAttribute("categorias", service.categorias());
            return "productos/form";
        }
        service.crear(producto);
        ra.addFlashAttribute("ok", "Producto creado correctamente.");
        return "redirect:/productos";
    }

    // FORM EDITAR
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        Producto p = service.obtener(id);
        model.addAttribute("producto", p);
        model.addAttribute("modo", "editar");
        model.addAttribute("categorias", service.categorias());
        return "productos/form";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         @ModelAttribute("producto") @Valid Producto producto,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("modo", "editar");
            model.addAttribute("categorias", service.categorias());
            return "productos/form";
        }
        service.actualizar(id, producto);
        ra.addFlashAttribute("ok", "Producto actualizado correctamente.");
        return "redirect:/productos";
    }

    // ELIMINAR (POST sencillo para no meternos con método DELETE en formularios)
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        service.eliminar(id);
        ra.addFlashAttribute("ok", "Producto eliminado.");
        return "redirect:/productos";
    }
}
