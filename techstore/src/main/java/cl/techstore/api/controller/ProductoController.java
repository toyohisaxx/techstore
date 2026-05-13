package cl.techstore.api.controller;

import cl.techstore.api.model.Producto;
import cl.techstore.api.service.ProductoService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // LISTAR
    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    // CREAR
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto guardar(@RequestBody Producto p) {
        return service.guardar(p);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id,
                              @RequestBody Producto p) {
        return service.actualizar(id, p);
    }

    // ELIMINADO LOGICO
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}