package es.curso.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.curso.jhipster.IntegrationTest;
import es.curso.jhipster.domain.Cine;
import es.curso.jhipster.repository.CineRepository;
import es.curso.jhipster.service.dto.CineDTO;
import es.curso.jhipster.service.mapper.CineMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CineResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DIRECCION = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DIRECCION = "BBBBBBBBBBBBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CineRepository cineRepository;

    @Autowired
    private CineMapper cineMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCineMockMvc;

    private Cine cine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cine createEntity(EntityManager em) {
        Cine cine = new Cine().nombre(DEFAULT_NOMBRE).direccion(DEFAULT_DIRECCION);
        return cine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cine createUpdatedEntity(EntityManager em) {
        Cine cine = new Cine().nombre(UPDATED_NOMBRE).direccion(UPDATED_DIRECCION);
        return cine;
    }

    @BeforeEach
    public void initTest() {
        cine = createEntity(em);
    }

    @Test
    @Transactional
    void createCine() throws Exception {
        int databaseSizeBeforeCreate = cineRepository.findAll().size();
        // Create the Cine
        CineDTO cineDTO = cineMapper.toDto(cine);
        restCineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cineDTO)))
            .andExpect(status().isCreated());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeCreate + 1);
        Cine testCine = cineList.get(cineList.size() - 1);
        assertThat(testCine.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testCine.getDireccion()).isEqualTo(DEFAULT_DIRECCION);
    }

    @Test
    @Transactional
    void createCineWithExistingId() throws Exception {
        // Create the Cine with an existing ID
        cine.setId(1L);
        CineDTO cineDTO = cineMapper.toDto(cine);

        int databaseSizeBeforeCreate = cineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCines() throws Exception {
        // Initialize the database
        cineRepository.saveAndFlush(cine);

        // Get all the cineList
        restCineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cine.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].direccion").value(hasItem(DEFAULT_DIRECCION)));
    }

    @Test
    @Transactional
    void getCine() throws Exception {
        // Initialize the database
        cineRepository.saveAndFlush(cine);

        // Get the cine
        restCineMockMvc
            .perform(get(ENTITY_API_URL_ID, cine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cine.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.direccion").value(DEFAULT_DIRECCION));
    }

    @Test
    @Transactional
    void getNonExistingCine() throws Exception {
        // Get the cine
        restCineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCine() throws Exception {
        // Initialize the database
        cineRepository.saveAndFlush(cine);

        int databaseSizeBeforeUpdate = cineRepository.findAll().size();

        // Update the cine
        Cine updatedCine = cineRepository.findById(cine.getId()).get();
        // Disconnect from session so that the updates on updatedCine are not directly saved in db
        em.detach(updatedCine);
        updatedCine.nombre(UPDATED_NOMBRE).direccion(UPDATED_DIRECCION);
        CineDTO cineDTO = cineMapper.toDto(updatedCine);

        restCineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cineDTO))
            )
            .andExpect(status().isOk());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
        Cine testCine = cineList.get(cineList.size() - 1);
        assertThat(testCine.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCine.getDireccion()).isEqualTo(UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    void putNonExistingCine() throws Exception {
        int databaseSizeBeforeUpdate = cineRepository.findAll().size();
        cine.setId(count.incrementAndGet());

        // Create the Cine
        CineDTO cineDTO = cineMapper.toDto(cine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCine() throws Exception {
        int databaseSizeBeforeUpdate = cineRepository.findAll().size();
        cine.setId(count.incrementAndGet());

        // Create the Cine
        CineDTO cineDTO = cineMapper.toDto(cine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCine() throws Exception {
        int databaseSizeBeforeUpdate = cineRepository.findAll().size();
        cine.setId(count.incrementAndGet());

        // Create the Cine
        CineDTO cineDTO = cineMapper.toDto(cine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCineWithPatch() throws Exception {
        // Initialize the database
        cineRepository.saveAndFlush(cine);

        int databaseSizeBeforeUpdate = cineRepository.findAll().size();

        // Update the cine using partial update
        Cine partialUpdatedCine = new Cine();
        partialUpdatedCine.setId(cine.getId());

        partialUpdatedCine.nombre(UPDATED_NOMBRE).direccion(UPDATED_DIRECCION);

        restCineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCine))
            )
            .andExpect(status().isOk());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
        Cine testCine = cineList.get(cineList.size() - 1);
        assertThat(testCine.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCine.getDireccion()).isEqualTo(UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    void fullUpdateCineWithPatch() throws Exception {
        // Initialize the database
        cineRepository.saveAndFlush(cine);

        int databaseSizeBeforeUpdate = cineRepository.findAll().size();

        // Update the cine using partial update
        Cine partialUpdatedCine = new Cine();
        partialUpdatedCine.setId(cine.getId());

        partialUpdatedCine.nombre(UPDATED_NOMBRE).direccion(UPDATED_DIRECCION);

        restCineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCine))
            )
            .andExpect(status().isOk());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
        Cine testCine = cineList.get(cineList.size() - 1);
        assertThat(testCine.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCine.getDireccion()).isEqualTo(UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    void patchNonExistingCine() throws Exception {
        int databaseSizeBeforeUpdate = cineRepository.findAll().size();
        cine.setId(count.incrementAndGet());

        // Create the Cine
        CineDTO cineDTO = cineMapper.toDto(cine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCine() throws Exception {
        int databaseSizeBeforeUpdate = cineRepository.findAll().size();
        cine.setId(count.incrementAndGet());

        // Create the Cine
        CineDTO cineDTO = cineMapper.toDto(cine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCine() throws Exception {
        int databaseSizeBeforeUpdate = cineRepository.findAll().size();
        cine.setId(count.incrementAndGet());

        // Create the Cine
        CineDTO cineDTO = cineMapper.toDto(cine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cine in the database
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCine() throws Exception {
        // Initialize the database
        cineRepository.saveAndFlush(cine);

        int databaseSizeBeforeDelete = cineRepository.findAll().size();

        // Delete the cine
        restCineMockMvc
            .perform(delete(ENTITY_API_URL_ID, cine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cine> cineList = cineRepository.findAll();
        assertThat(cineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
