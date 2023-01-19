package es.curso.jhipster.repository;

import es.curso.jhipster.domain.Cine;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CineRepository extends JpaRepository<Cine, Long> {}
