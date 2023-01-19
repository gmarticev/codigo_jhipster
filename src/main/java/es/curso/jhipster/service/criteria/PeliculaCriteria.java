package es.curso.jhipster.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link es.curso.jhipster.domain.Pelicula} entity. This class is used
 * in {@link es.curso.jhipster.web.rest.PeliculaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /peliculas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PeliculaCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titulo;

    private InstantFilter fechaEstreno;

    private StringFilter descripcion;

    private BooleanFilter enCines;

    private LongFilter estrenoId;

    private LongFilter directorId;

    private LongFilter actorId;

    private Boolean distinct;

    public PeliculaCriteria() {}

    public PeliculaCriteria(PeliculaCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.titulo = other.titulo == null ? null : other.titulo.copy();
        this.fechaEstreno = other.fechaEstreno == null ? null : other.fechaEstreno.copy();
        this.descripcion = other.descripcion == null ? null : other.descripcion.copy();
        this.enCines = other.enCines == null ? null : other.enCines.copy();
        this.estrenoId = other.estrenoId == null ? null : other.estrenoId.copy();
        this.directorId = other.directorId == null ? null : other.directorId.copy();
        this.actorId = other.actorId == null ? null : other.actorId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PeliculaCriteria copy() {
        return new PeliculaCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitulo() {
        return titulo;
    }

    public StringFilter titulo() {
        if (titulo == null) {
            titulo = new StringFilter();
        }
        return titulo;
    }

    public void setTitulo(StringFilter titulo) {
        this.titulo = titulo;
    }

    public InstantFilter getFechaEstreno() {
        return fechaEstreno;
    }

    public InstantFilter fechaEstreno() {
        if (fechaEstreno == null) {
            fechaEstreno = new InstantFilter();
        }
        return fechaEstreno;
    }

    public void setFechaEstreno(InstantFilter fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public StringFilter getDescripcion() {
        return descripcion;
    }

    public StringFilter descripcion() {
        if (descripcion == null) {
            descripcion = new StringFilter();
        }
        return descripcion;
    }

    public void setDescripcion(StringFilter descripcion) {
        this.descripcion = descripcion;
    }

    public BooleanFilter getEnCines() {
        return enCines;
    }

    public BooleanFilter enCines() {
        if (enCines == null) {
            enCines = new BooleanFilter();
        }
        return enCines;
    }

    public void setEnCines(BooleanFilter enCines) {
        this.enCines = enCines;
    }

    public LongFilter getEstrenoId() {
        return estrenoId;
    }

    public LongFilter estrenoId() {
        if (estrenoId == null) {
            estrenoId = new LongFilter();
        }
        return estrenoId;
    }

    public void setEstrenoId(LongFilter estrenoId) {
        this.estrenoId = estrenoId;
    }

    public LongFilter getDirectorId() {
        return directorId;
    }

    public LongFilter directorId() {
        if (directorId == null) {
            directorId = new LongFilter();
        }
        return directorId;
    }

    public void setDirectorId(LongFilter directorId) {
        this.directorId = directorId;
    }

    public LongFilter getActorId() {
        return actorId;
    }

    public LongFilter actorId() {
        if (actorId == null) {
            actorId = new LongFilter();
        }
        return actorId;
    }

    public void setActorId(LongFilter actorId) {
        this.actorId = actorId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PeliculaCriteria that = (PeliculaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titulo, that.titulo) &&
            Objects.equals(fechaEstreno, that.fechaEstreno) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(enCines, that.enCines) &&
            Objects.equals(estrenoId, that.estrenoId) &&
            Objects.equals(directorId, that.directorId) &&
            Objects.equals(actorId, that.actorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, fechaEstreno, descripcion, enCines, estrenoId, directorId, actorId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PeliculaCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (titulo != null ? "titulo=" + titulo + ", " : "") +
            (fechaEstreno != null ? "fechaEstreno=" + fechaEstreno + ", " : "") +
            (descripcion != null ? "descripcion=" + descripcion + ", " : "") +
            (enCines != null ? "enCines=" + enCines + ", " : "") +
            (estrenoId != null ? "estrenoId=" + estrenoId + ", " : "") +
            (directorId != null ? "directorId=" + directorId + ", " : "") +
            (actorId != null ? "actorId=" + actorId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
