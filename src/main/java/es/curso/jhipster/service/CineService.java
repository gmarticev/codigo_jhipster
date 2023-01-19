package es.curso.jhipster.service;

import es.curso.jhipster.domain.Cine;
import es.curso.jhipster.repository.CineRepository;
import es.curso.jhipster.service.dto.CineDTO;
import es.curso.jhipster.service.mapper.CineMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Cine}.
 */
@Service
@Transactional
public class CineService {

    private final Logger log = LoggerFactory.getLogger(CineService.class);

    private final CineRepository cineRepository;

    private final CineMapper cineMapper;

    public CineService(CineRepository cineRepository, CineMapper cineMapper) {
        this.cineRepository = cineRepository;
        this.cineMapper = cineMapper;
    }

    /**
     * Save a cine.
     *
     * @param cineDTO the entity to save.
     * @return the persisted entity.
     */
    public CineDTO save(CineDTO cineDTO) {
        log.debug("Request to save Cine : {}", cineDTO);
        Cine cine = cineMapper.toEntity(cineDTO);
        cine = cineRepository.save(cine);
        return cineMapper.toDto(cine);
    }

    /**
     * Update a cine.
     *
     * @param cineDTO the entity to save.
     * @return the persisted entity.
     */
    public CineDTO update(CineDTO cineDTO) {
        log.debug("Request to update Cine : {}", cineDTO);
        Cine cine = cineMapper.toEntity(cineDTO);
        cine = cineRepository.save(cine);
        return cineMapper.toDto(cine);
    }

    /**
     * Partially update a cine.
     *
     * @param cineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CineDTO> partialUpdate(CineDTO cineDTO) {
        log.debug("Request to partially update Cine : {}", cineDTO);

        return cineRepository
            .findById(cineDTO.getId())
            .map(existingCine -> {
                cineMapper.partialUpdate(existingCine, cineDTO);

                return existingCine;
            })
            .map(cineRepository::save)
            .map(cineMapper::toDto);
    }

    /**
     * Get all the cines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CineDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Cines");
        return cineRepository.findAll(pageable).map(cineMapper::toDto);
    }

    /**
     * Get one cine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CineDTO> findOne(Long id) {
        log.debug("Request to get Cine : {}", id);
        return cineRepository.findById(id).map(cineMapper::toDto);
    }

    /**
     * Delete the cine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Cine : {}", id);
        cineRepository.deleteById(id);
    }
}
