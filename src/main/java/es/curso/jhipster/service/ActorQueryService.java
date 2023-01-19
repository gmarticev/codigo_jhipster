package es.curso.jhipster.service;

import es.curso.jhipster.domain.*; // for static metamodels
import es.curso.jhipster.domain.Actor;
import es.curso.jhipster.repository.ActorRepository;
import es.curso.jhipster.service.criteria.ActorCriteria;
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
 * Service for executing complex queries for {@link Actor} entities in the database.
 * The main input is a {@link ActorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Actor} or a {@link Page} of {@link Actor} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ActorQueryService extends QueryService<Actor> {

    private final Logger log = LoggerFactory.getLogger(ActorQueryService.class);

    private final ActorRepository actorRepository;

    public ActorQueryService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    /**
     * Return a {@link List} of {@link Actor} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Actor> findByCriteria(ActorCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Actor> specification = createSpecification(criteria);
        return actorRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Actor} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Actor> findByCriteria(ActorCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Actor> specification = createSpecification(criteria);
        return actorRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ActorCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Actor> specification = createSpecification(criteria);
        return actorRepository.count(specification);
    }

    /**
     * Function to convert {@link ActorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Actor> createSpecification(ActorCriteria criteria) {
        Specification<Actor> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Actor_.id));
            }
            if (criteria.getNombre() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNombre(), Actor_.nombre));
            }
            if (criteria.getApellidos() != null) {
                specification = specification.and(buildStringSpecification(criteria.getApellidos(), Actor_.apellidos));
            }
            if (criteria.getFechaNacimiento() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFechaNacimiento(), Actor_.fechaNacimiento));
            }
            if (criteria.getPeliculaId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPeliculaId(), root -> root.join(Actor_.peliculas, JoinType.LEFT).get(Pelicula_.id))
                    );
            }
        }
        return specification;
    }
}
