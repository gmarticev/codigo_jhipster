package es.curso.jhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Pelicula.
 */
@Entity
@Table(name = "pelicula")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pelicula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 4, max = 50)
    @Column(name = "titulo", length = 50, nullable = false)
    private String titulo;

    @Column(name = "fecha_estreno")
    private Instant fechaEstreno;

    @Size(min = 20, max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "en_cines")
    private Boolean enCines;

    @JsonIgnoreProperties(value = { "pelicula" }, allowSetters = true)
    @OneToOne(mappedBy = "pelicula")
    private Estreno estreno;

    @ManyToOne
    @JsonIgnoreProperties(value = { "peliculas" }, allowSetters = true)
    private Director director;

    @ManyToMany
    @JoinTable(
        name = "rel_pelicula__actor",
        joinColumns = @JoinColumn(name = "pelicula_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "peliculas" }, allowSetters = true)
    private Set<Actor> actors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pelicula id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Pelicula titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Instant getFechaEstreno() {
        return this.fechaEstreno;
    }

    public Pelicula fechaEstreno(Instant fechaEstreno) {
        this.setFechaEstreno(fechaEstreno);
        return this;
    }

    public void setFechaEstreno(Instant fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Pelicula descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEnCines() {
        return this.enCines;
    }

    public Pelicula enCines(Boolean enCines) {
        this.setEnCines(enCines);
        return this;
    }

    public void setEnCines(Boolean enCines) {
        this.enCines = enCines;
    }

    public Estreno getEstreno() {
        return this.estreno;
    }

    public void setEstreno(Estreno estreno) {
        if (this.estreno != null) {
            this.estreno.setPelicula(null);
        }
        if (estreno != null) {
            estreno.setPelicula(this);
        }
        this.estreno = estreno;
    }

    public Pelicula estreno(Estreno estreno) {
        this.setEstreno(estreno);
        return this;
    }

    public Director getDirector() {
        return this.director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public Pelicula director(Director director) {
        this.setDirector(director);
        return this;
    }

    public Set<Actor> getActors() {
        return this.actors;
    }

    public void setActors(Set<Actor> actors) {
        this.actors = actors;
    }

    public Pelicula actors(Set<Actor> actors) {
        this.setActors(actors);
        return this;
    }

    public Pelicula addActor(Actor actor) {
        this.actors.add(actor);
        actor.getPeliculas().add(this);
        return this;
    }

    public Pelicula removeActor(Actor actor) {
        this.actors.remove(actor);
        actor.getPeliculas().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pelicula)) {
            return false;
        }
        return id != null && id.equals(((Pelicula) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pelicula{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", fechaEstreno='" + getFechaEstreno() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", enCines='" + getEnCines() + "'" +
            "}";
    }
}
