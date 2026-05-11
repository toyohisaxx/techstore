package com.techstore.techstore.service;

import com.techstore.techstore.model.Product;
import com.techstore.techstore.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> listar() {
        return repository.findByActivoTrue();
    }

    public Product guardar(Product p) {
        return repository.save(p);
    }

    public Product actualizar(Long id, Product p) {

        Product producto = repository.findById(id).orElseThrow();

        producto.setNombre(p.getNombre());
        producto.setPrecio(p.getPrecio());
        producto.setStock(p.getStock());

        return repository.save(producto);
    }

    public void eliminar(Long id) {

        Product producto = repository.findById(id).orElseThrow();

        producto.setActivo(false);

        repository.save(producto);
    }
}