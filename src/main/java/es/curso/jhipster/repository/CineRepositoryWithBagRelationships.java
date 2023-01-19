package es.curso.jhipster.repository;

import es.curso.jhipster.domain.Cine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CineRepositoryWithBagRelationships {
    Optional<Cine> fetchBagRelationships(Optional<Cine> cine);

    List<Cine> fetchBagRelationships(List<Cine> cines);

    Page<Cine> fetchBagRelationships(Page<Cine> cines);
}
