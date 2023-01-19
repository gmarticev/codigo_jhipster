package es.curso.jhipster.repository;

import es.curso.jhipster.domain.Pelicula;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PeliculaRepositoryWithBagRelationships {
    Optional<Pelicula> fetchBagRelationships(Optional<Pelicula> pelicula);

    List<Pelicula> fetchBagRelationships(List<Pelicula> peliculas);

    Page<Pelicula> fetchBagRelationships(Page<Pelicula> peliculas);
}
