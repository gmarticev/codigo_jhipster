package es.curso.jhipster.repository;

import es.curso.jhipster.domain.Pelicula;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PeliculaRepositoryWithBagRelationshipsImpl implements PeliculaRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Pelicula> fetchBagRelationships(Optional<Pelicula> pelicula) {
        return pelicula.map(this::fetchActors);
    }

    @Override
    public Page<Pelicula> fetchBagRelationships(Page<Pelicula> peliculas) {
        return new PageImpl<>(fetchBagRelationships(peliculas.getContent()), peliculas.getPageable(), peliculas.getTotalElements());
    }

    @Override
    public List<Pelicula> fetchBagRelationships(List<Pelicula> peliculas) {
        return Optional.of(peliculas).map(this::fetchActors).orElse(Collections.emptyList());
    }

    Pelicula fetchActors(Pelicula result) {
        return entityManager
            .createQuery(
                "select pelicula from Pelicula pelicula left join fetch pelicula.actors where pelicula is :pelicula",
                Pelicula.class
            )
            .setParameter("pelicula", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Pelicula> fetchActors(List<Pelicula> peliculas) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, peliculas.size()).forEach(index -> order.put(peliculas.get(index).getId(), index));
        List<Pelicula> result = entityManager
            .createQuery(
                "select distinct pelicula from Pelicula pelicula left join fetch pelicula.actors where pelicula in :peliculas",
                Pelicula.class
            )
            .setParameter("peliculas", peliculas)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
