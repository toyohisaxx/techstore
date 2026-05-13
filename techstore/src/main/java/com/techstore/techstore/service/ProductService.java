package com.techstore.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techstore.techstore.model.Product;
import com.techstore.techstore.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // LISTAR PRODUCTOS ACTIVOS
    public List<Product> listar() {
        return repository.findByActivoTrue();
    }

    // GUARDAR PRODUCTO
    public Product guardar(Product p) {
        return repository.save(p);
    }

    // ACTUALIZAR PRODUCTO
    public Product actualizar(Long id, Product p) {

        Product producto = repository.findById(id)
                .orElseThrow();

        producto.setNombre(p.getNombre());
        producto.setPrecio(p.getPrecio());
        producto.setStock(p.getStock());
        producto.setDescripcion(p.getDescripcion());
        producto.setCategoria(p.getCategoria());

        return repository.save(producto);
    }

    // ELIMINADO LOGICO
    public void eliminar(Long id) {

        Product producto = repository.findById(id)
                .orElseThrow();

        producto.setActivo(false);

        repository.save(producto);
    }
}