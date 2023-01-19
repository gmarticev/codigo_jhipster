package es.curso.jhipster.repository;

import es.curso.jhipster.domain.Pelicula;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Pelicula entity.
 *
 * When extending this class, extend PeliculaRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface PeliculaRepository
    extends PeliculaRepositoryWithBagRelationships, JpaRepository<Pelicula, Long>, JpaSpecificationExecutor<Pelicula> {
    default Optional<Pelicula> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Pelicula> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Pelicula> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct pelicula from Pelicula pelicula left join fetch pelicula.director",
        countQuery = "select count(distinct pelicula) from Pelicula pelicula"
    )
    Page<Pelicula> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct pelicula from Pelicula pelicula left join fetch pelicula.director")
    List<Pelicula> findAllWithToOneRelationships();

    @Query("select pelicula from Pelicula pelicula left join fetch pelicula.director where pelicula.id =:id")
    Optional<Pelicula> findOneWithToOneRelationships(@Param("id") Long id);
}
