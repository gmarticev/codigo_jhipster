package es.curso.jhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Estreno.
 */
@Entity
@Table(name = "estreno")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Estreno implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fecha")
    private Instant fecha;

    @Size(min = 4, max = 150)
    @Column(name = "lugar", length = 150)
    private String lugar;

    @JsonIgnoreProperties(value = { "estreno", "director", "actors" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Pelicula pelicula;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Estreno id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFecha() {
        return this.fecha;
    }

    public Estreno fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return this.lugar;
    }

    public Estreno lugar(String lugar) {
        this.setLugar(lugar);
        return this;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Pelicula getPelicula() {
        return this.pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Estreno pelicula(Pelicula pelicula) {
        this.setPelicula(pelicula);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Estreno)) {
            return false;
        }
        return id != null && id.equals(((Estreno) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Estreno{" +
            "id=" + getId() +
            ", fecha='" + getFecha() + "'" +
            ", lugar='" + getLugar() + "'" +
            "}";
    }
}
