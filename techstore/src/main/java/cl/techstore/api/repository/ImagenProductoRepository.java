package cl.techstore.api.repository;

import cl.techstore.api.model.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {
}