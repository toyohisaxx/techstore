package cl.techstore.api.service;

import java.util.List;
import org.springframework.stereotype.Service;
import cl.techstore.api.model.Producto;
import cl.techstore.api.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findByActivoTrue();
    }

    public Producto guardar(Producto p) {
        return repository.save(p);
    }

    public Producto buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Producto actualizar(Long id, Producto p) {
        Producto producto = repository.findById(id).orElseThrow();
        producto.setNombre(p.getNombre());
        producto.setPrecio(p.getPrecio());
        producto.setStock(p.getStock());
        producto.setDescripcion(p.getDescripcion());
        producto.setCategoria(p.getCategoria());
        return repository.save(producto);
    }

    public void eliminar(Long id) {
        Producto producto = repository.findById(id).orElseThrow();
        producto.setActivo(false);
        repository.save(producto);
    }
}