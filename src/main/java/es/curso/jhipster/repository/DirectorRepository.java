package es.curso.jhipster.repository;

import es.curso.jhipster.domain.Director;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Director entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {}
