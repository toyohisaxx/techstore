package cl.techstore.api.controller;

import cl.techstore.api.model.Producto;
import cl.techstore.api.service.AuditService;
import cl.techstore.api.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;
    private final AuditService auditService;

    public ProductoController(ProductoService service, AuditService auditService) {
        this.service = service;
        this.auditService = auditService;
    }

    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto guardar(@RequestBody Producto p, Authentication auth) {
        Producto creado = service.guardar(p);
        auditService.publicarAuditoria("CREAR", creado.getId(), creado.getNombre(), auth.getName());
        return creado;
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto p, Authentication auth) {
        Producto actualizado = service.actualizar(id, p);
        auditService.publicarAuditoria("MODIFICAR", actualizado.getId(), actualizado.getNombre(), auth.getName());
        return actualizado;
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id, Authentication auth) {
        Producto producto = service.buscarPorId(id);
        service.eliminar(id);
        auditService.publicarAuditoria("ELIMINAR", id, producto.getNombre(), auth.getName());
    }
}