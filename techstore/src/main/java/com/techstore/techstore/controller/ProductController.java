package com.techstore.techstore.controller;

import com.techstore.techstore.model.Product;
import com.techstore.techstore.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> listar() {
        return service.listar();
    }

    @PostMapping
    public Product crear(@RequestBody Product p) {
        return service.guardar(p);
    }

    @PutMapping("/{id}")
    public Product actualizar(
            @PathVariable Long id,
            @RequestBody Product p) {

        return service.actualizar(id, p);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}