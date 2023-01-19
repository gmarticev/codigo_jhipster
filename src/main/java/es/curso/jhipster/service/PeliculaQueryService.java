package es.curso.jhipster.service;

import es.curso.jhipster.domain.*; // for static metamodels
import es.curso.jhipster.domain.Pelicula;
import es.curso.jhipster.repository.PeliculaRepository;
import es.curso.jhipster.service.criteria.PeliculaCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Pelicula} entities in the database.
 * The main input is a {@link PeliculaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Pelicula} or a {@link Page} of {@link Pelicula} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PeliculaQueryService extends QueryService<Pelicula> {

    private final Logger log = LoggerFactory.getLogger(PeliculaQueryService.class);

    private final PeliculaRepository peliculaRepository;

    public PeliculaQueryService(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    /**
     * Return a {@link List} of {@link Pelicula} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Pelicula> findByCriteria(PeliculaCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Pelicula> specification = createSpecification(criteria);
        return peliculaRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Pelicula} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Pelicula> findByCriteria(PeliculaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pelicula> specification = createSpecification(criteria);
        return peliculaRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PeliculaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Pelicula> specification = createSpecification(criteria);
        return peliculaRepository.count(specification);
    }

    /**
     * Function to convert {@link PeliculaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Pelicula> createSpecification(PeliculaCriteria criteria) {
        Specification<Pelicula> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Pelicula_.id));
            }
            if (criteria.getTitulo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitulo(), Pelicula_.titulo));
            }
            if (criteria.getFechaEstreno() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFechaEstreno(), Pelicula_.fechaEstreno));
            }
            if (criteria.getDescripcion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescripcion(), Pelicula_.descripcion));
            }
            if (criteria.getEnCines() != null) {
                specification = specification.and(buildSpecification(criteria.getEnCines(), Pelicula_.enCines));
            }
            if (criteria.getEstrenoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEstrenoId(), root -> root.join(Pelicula_.estreno, JoinType.LEFT).get(Estreno_.id))
                    );
            }
            if (criteria.getDirectorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDirectorId(), root -> root.join(Pelicula_.director, JoinType.LEFT).get(Director_.id))
                    );
            }
            if (criteria.getActorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getActorId(), root -> root.join(Pelicula_.actors, JoinType.LEFT).get(Actor_.id))
                    );
            }
        }
        return specification;
    }
}
