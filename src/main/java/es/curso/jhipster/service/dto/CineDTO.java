package es.curso.jhipster.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link es.curso.jhipster.domain.Cine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CineDTO implements Serializable {

    private Long id;

    @Size(min = 5, max = 50)
    private String nombre;

    @Size(min = 20, max = 500)
    private String direccion;

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CineDTO)) {
            return false;
        }

        CineDTO cineDTO = (CineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CineDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", direccion='" + getDireccion() + "'" +
            "}";
    }
}
