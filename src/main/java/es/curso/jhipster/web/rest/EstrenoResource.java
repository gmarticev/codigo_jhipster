package es.curso.jhipster.web.rest;

import es.curso.jhipster.domain.Estreno;
import es.curso.jhipster.repository.EstrenoRepository;
import es.curso.jhipster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link es.curso.jhipster.domain.Estreno}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EstrenoResource {

    private final Logger log = LoggerFactory.getLogger(EstrenoResource.class);

    private static final String ENTITY_NAME = "estreno";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EstrenoRepository estrenoRepository;

    public EstrenoResource(EstrenoRepository estrenoRepository) {
        this.estrenoRepository = estrenoRepository;
    }

    /**
     * {@code POST  /estrenos} : Create a new estreno.
     *
     * @param estreno the estreno to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new estreno, or with status {@code 400 (Bad Request)} if the estreno has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/estrenos")
    public ResponseEntity<Estreno> createEstreno(@Valid @RequestBody Estreno estreno) throws URISyntaxException {
        log.debug("REST request to save Estreno : {}", estreno);
        if (estreno.getId() != null) {
            throw new BadRequestAlertException("A new estreno cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Estreno result = estrenoRepository.save(estreno);
        return ResponseEntity
            .created(new URI("/api/estrenos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /estrenos/:id} : Updates an existing estreno.
     *
     * @param id the id of the estreno to save.
     * @param estreno the estreno to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estreno,
     * or with status {@code 400 (Bad Request)} if the estreno is not valid,
     * or with status {@code 500 (Internal Server Error)} if the estreno couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/estrenos/{id}")
    public ResponseEntity<Estreno> updateEstreno(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Estreno estreno
    ) throws URISyntaxException {
        log.debug("REST request to update Estreno : {}, {}", id, estreno);
        if (estreno.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, estreno.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!estrenoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Estreno result = estrenoRepository.save(estreno);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, estreno.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /estrenos/:id} : Partial updates given fields of an existing estreno, field will ignore if it is null
     *
     * @param id the id of the estreno to save.
     * @param estreno the estreno to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estreno,
     * or with status {@code 400 (Bad Request)} if the estreno is not valid,
     * or with status {@code 404 (Not Found)} if the estreno is not found,
     * or with status {@code 500 (Internal Server Error)} if the estreno couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/estrenos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Estreno> partialUpdateEstreno(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Estreno estreno
    ) throws URISyntaxException {
        log.debug("REST request to partial update Estreno partially : {}, {}", id, estreno);
        if (estreno.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, estreno.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!estrenoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Estreno> result = estrenoRepository
            .findById(estreno.getId())
            .map(existingEstreno -> {
                if (estreno.getFecha() != null) {
                    existingEstreno.setFecha(estreno.getFecha());
                }
                if (estreno.getLugar() != null) {
                    existingEstreno.setLugar(estreno.getLugar());
                }

                return existingEstreno;
            })
            .map(estrenoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, estreno.getId().toString())
        );
    }

    /**
     * {@code GET  /estrenos} : get all the estrenos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of estrenos in body.
     */
    @GetMapping("/estrenos")
    public ResponseEntity<List<Estreno>> getAllEstrenos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Estrenos");
        Page<Estreno> page;
        if (eagerload) {
            page = estrenoRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = estrenoRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /estrenos/:id} : get the "id" estreno.
     *
     * @param id the id of the estreno to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the estreno, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/estrenos/{id}")
    public ResponseEntity<Estreno> getEstreno(@PathVariable Long id) {
        log.debug("REST request to get Estreno : {}", id);
        Optional<Estreno> estreno = estrenoRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(estreno);
    }

    /**
     * {@code DELETE  /estrenos/:id} : delete the "id" estreno.
     *
     * @param id the id of the estreno to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/estrenos/{id}")
    public ResponseEntity<Void> deleteEstreno(@PathVariable Long id) {
        log.debug("REST request to delete Estreno : {}", id);
        estrenoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
