package es.curso.jhipster.web.rest;

import es.curso.jhipster.domain.Pelicula;
import es.curso.jhipster.repository.PeliculaRepository;
import es.curso.jhipster.service.PeliculaQueryService;
import es.curso.jhipster.service.PeliculaService;
import es.curso.jhipster.service.criteria.PeliculaCriteria;
import es.curso.jhipster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link es.curso.jhipster.domain.Pelicula}.
 */
@RestController
@RequestMapping("/api")
public class PeliculaResource {

    private final Logger log = LoggerFactory.getLogger(PeliculaResource.class);

    private static final String ENTITY_NAME = "pelicula";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PeliculaService peliculaService;

    private final PeliculaRepository peliculaRepository;

    private final PeliculaQueryService peliculaQueryService;

    public PeliculaResource(
        PeliculaService peliculaService,
        PeliculaRepository peliculaRepository,
        PeliculaQueryService peliculaQueryService
    ) {
        this.peliculaService = peliculaService;
        this.peliculaRepository = peliculaRepository;
        this.peliculaQueryService = peliculaQueryService;
    }

    /**
     * {@code POST  /peliculas} : Create a new pelicula.
     *
     * @param pelicula the pelicula to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pelicula, or with status {@code 400 (Bad Request)} if the pelicula has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/peliculas")
    public ResponseEntity<Pelicula> createPelicula(@Valid @RequestBody Pelicula pelicula) throws URISyntaxException {
        log.debug("REST request to save Pelicula : {}", pelicula);
        if (pelicula.getId() != null) {
            throw new BadRequestAlertException("A new pelicula cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Pelicula result = peliculaService.save(pelicula);
        return ResponseEntity
            .created(new URI("/api/peliculas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /peliculas/:id} : Updates an existing pelicula.
     *
     * @param id the id of the pelicula to save.
     * @param pelicula the pelicula to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pelicula,
     * or with status {@code 400 (Bad Request)} if the pelicula is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pelicula couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/peliculas/{id}")
    public ResponseEntity<Pelicula> updatePelicula(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Pelicula pelicula
    ) throws URISyntaxException {
        log.debug("REST request to update Pelicula : {}, {}", id, pelicula);
        if (pelicula.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pelicula.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!peliculaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Pelicula result = peliculaService.update(pelicula);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pelicula.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /peliculas/:id} : Partial updates given fields of an existing pelicula, field will ignore if it is null
     *
     * @param id the id of the pelicula to save.
     * @param pelicula the pelicula to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pelicula,
     * or with status {@code 400 (Bad Request)} if the pelicula is not valid,
     * or with status {@code 404 (Not Found)} if the pelicula is not found,
     * or with status {@code 500 (Internal Server Error)} if the pelicula couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/peliculas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Pelicula> partialUpdatePelicula(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Pelicula pelicula
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pelicula partially : {}, {}", id, pelicula);
        if (pelicula.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pelicula.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!peliculaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Pelicula> result = peliculaService.partialUpdate(pelicula);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pelicula.getId().toString())
        );
    }

    /**
     * {@code GET  /peliculas} : get all the peliculas.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of peliculas in body.
     */
    @GetMapping("/peliculas")
    public ResponseEntity<List<Pelicula>> getAllPeliculas(
        PeliculaCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Peliculas by criteria: {}", criteria);
        Page<Pelicula> page = peliculaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /peliculas/count} : count all the peliculas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/peliculas/count")
    public ResponseEntity<Long> countPeliculas(PeliculaCriteria criteria) {
        log.debug("REST request to count Peliculas by criteria: {}", criteria);
        return ResponseEntity.ok().body(peliculaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /peliculas/:id} : get the "id" pelicula.
     *
     * @param id the id of the pelicula to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pelicula, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/peliculas/{id}")
    public ResponseEntity<Pelicula> getPelicula(@PathVariable Long id) {
        log.debug("REST request to get Pelicula : {}", id);
        Optional<Pelicula> pelicula = peliculaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pelicula);
    }

    /**
     * {@code DELETE  /peliculas/:id} : delete the "id" pelicula.
     *
     * @param id the id of the pelicula to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/peliculas/{id}")
    public ResponseEntity<Void> deletePelicula(@PathVariable Long id) {
        log.debug("REST request to delete Pelicula : {}", id);
        peliculaService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
