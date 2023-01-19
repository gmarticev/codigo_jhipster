package es.curso.jhipster.repository;

import es.curso.jhipster.domain.Estreno;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Estreno entity.
 */
@Repository
public interface EstrenoRepository extends JpaRepository<Estreno, Long> {
    default Optional<Estreno> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Estreno> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Estreno> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct estreno from Estreno estreno left join fetch estreno.pelicula",
        countQuery = "select count(distinct estreno) from Estreno estreno"
    )
    Page<Estreno> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct estreno from Estreno estreno left join fetch estreno.pelicula")
    List<Estreno> findAllWithToOneRelationships();

    @Query("select estreno from Estreno estreno left join fetch estreno.pelicula where estreno.id =:id")
    Optional<Estreno> findOneWithToOneRelationships(@Param("id") Long id);
}
