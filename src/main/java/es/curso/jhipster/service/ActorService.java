package es.curso.jhipster.service;

import es.curso.jhipster.domain.Actor;
import es.curso.jhipster.repository.ActorRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Actor}.
 */
@Service
@Transactional
public class ActorService {

    private final Logger log = LoggerFactory.getLogger(ActorService.class);

    private final ActorRepository actorRepository;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    /**
     * Save a actor.
     *
     * @param actor the entity to save.
     * @return the persisted entity.
     */
    public Actor save(Actor actor) {
        log.debug("Request to save Actor : {}", actor);
        return actorRepository.save(actor);
    }

    /**
     * Update a actor.
     *
     * @param actor the entity to save.
     * @return the persisted entity.
     */
    public Actor update(Actor actor) {
        log.debug("Request to update Actor : {}", actor);
        return actorRepository.save(actor);
    }

    /**
     * Partially update a actor.
     *
     * @param actor the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Actor> partialUpdate(Actor actor) {
        log.debug("Request to partially update Actor : {}", actor);

        return actorRepository
            .findById(actor.getId())
            .map(existingActor -> {
                if (actor.getNombre() != null) {
                    existingActor.setNombre(actor.getNombre());
                }
                if (actor.getApellidos() != null) {
                    existingActor.setApellidos(actor.getApellidos());
                }
                if (actor.getFechaNacimiento() != null) {
                    existingActor.setFechaNacimiento(actor.getFechaNacimiento());
                }

                return existingActor;
            })
            .map(actorRepository::save);
    }

    /**
     * Get all the actors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Actor> findAll(Pageable pageable) {
        log.debug("Request to get all Actors");
        return actorRepository.findAll(pageable);
    }

    /**
     * Get one actor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Actor> findOne(Long id) {
        log.debug("Request to get Actor : {}", id);
        return actorRepository.findById(id);
    }

    /**
     * Delete the actor by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Actor : {}", id);
        actorRepository.deleteById(id);
    }
}
