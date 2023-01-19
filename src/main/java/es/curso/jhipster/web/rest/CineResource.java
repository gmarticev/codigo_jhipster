package es.curso.jhipster.web.rest;

import es.curso.jhipster.repository.CineRepository;
import es.curso.jhipster.service.CineService;
import es.curso.jhipster.service.dto.CineDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link es.curso.jhipster.domain.Cine}.
 */
@RestController
@RequestMapping("/api")
public class CineResource {

    private final Logger log = LoggerFactory.getLogger(CineResource.class);

    private static final String ENTITY_NAME = "cine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CineService cineService;

    private final CineRepository cineRepository;

    public CineResource(CineService cineService, CineRepository cineRepository) {
        this.cineService = cineService;
        this.cineRepository = cineRepository;
    }

    /**
     * {@code POST  /cines} : Create a new cine.
     *
     * @param cineDTO the cineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cineDTO, or with status {@code 400 (Bad Request)} if the cine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cines")
    public ResponseEntity<CineDTO> createCine(@Valid @RequestBody CineDTO cineDTO) throws URISyntaxException {
        log.debug("REST request to save Cine : {}", cineDTO);
        if (cineDTO.getId() != null) {
            throw new BadRequestAlertException("A new cine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CineDTO result = cineService.save(cineDTO);
        return ResponseEntity
            .created(new URI("/api/cines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cines/:id} : Updates an existing cine.
     *
     * @param id the id of the cineDTO to save.
     * @param cineDTO the cineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cineDTO,
     * or with status {@code 400 (Bad Request)} if the cineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cines/{id}")
    public ResponseEntity<CineDTO> updateCine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CineDTO cineDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Cine : {}, {}", id, cineDTO);
        if (cineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CineDTO result = cineService.update(cineDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cineDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cines/:id} : Partial updates given fields of an existing cine, field will ignore if it is null
     *
     * @param id the id of the cineDTO to save.
     * @param cineDTO the cineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cineDTO,
     * or with status {@code 400 (Bad Request)} if the cineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cines/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CineDTO> partialUpdateCine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CineDTO cineDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cine partially : {}, {}", id, cineDTO);
        if (cineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CineDTO> result = cineService.partialUpdate(cineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cines} : get all the cines.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cines in body.
     */
    @GetMapping("/cines")
    public ResponseEntity<List<CineDTO>> getAllCines(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Cines");
        Page<CineDTO> page = cineService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cines/:id} : get the "id" cine.
     *
     * @param id the id of the cineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cines/{id}")
    public ResponseEntity<CineDTO> getCine(@PathVariable Long id) {
        log.debug("REST request to get Cine : {}", id);
        Optional<CineDTO> cineDTO = cineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cineDTO);
    }

    /**
     * {@code DELETE  /cines/:id} : delete the "id" cine.
     *
     * @param id the id of the cineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cines/{id}")
    public ResponseEntity<Void> deleteCine(@PathVariable Long id) {
        log.debug("REST request to delete Cine : {}", id);
        cineService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
