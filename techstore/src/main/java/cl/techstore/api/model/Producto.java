package cl.techstore.api.model;


import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Producto {
    

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<ImagenProducto> imagenes;
    public List<ImagenProducto> getImagenes() {
       return imagenes;
    }

    public void setImagenes(List<ImagenProducto> imagenes) {
       this.imagenes = imagenes;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Double precio;

    private Integer stock;

    private String descripcion;

    private String categoria;

    private Boolean activo = true;

    public Producto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}