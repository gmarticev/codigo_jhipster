package es.curso.jhipster.repository;

import es.curso.jhipster.domain.Cine;
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
public class CineRepositoryWithBagRelationshipsImpl implements CineRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Cine> fetchBagRelationships(Optional<Cine> cine) {
        return cine.map(this::fetchPeliculas);
    }

    @Override
    public Page<Cine> fetchBagRelationships(Page<Cine> cines) {
        return new PageImpl<>(fetchBagRelationships(cines.getContent()), cines.getPageable(), cines.getTotalElements());
    }

    @Override
    public List<Cine> fetchBagRelationships(List<Cine> cines) {
        return Optional.of(cines).map(this::fetchPeliculas).orElse(Collections.emptyList());
    }

    Cine fetchPeliculas(Cine result) {
        return entityManager
            .createQuery("select cine from Cine cine left join fetch cine.peliculas where cine is :cine", Cine.class)
            .setParameter("cine", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Cine> fetchPeliculas(List<Cine> cines) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, cines.size()).forEach(index -> order.put(cines.get(index).getId(), index));
        List<Cine> result = entityManager
            .createQuery("select distinct cine from Cine cine left join fetch cine.peliculas where cine in :cines", Cine.class)
            .setParameter("cines", cines)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
