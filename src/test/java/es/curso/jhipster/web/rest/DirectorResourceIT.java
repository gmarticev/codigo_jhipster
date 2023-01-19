package es.curso.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.curso.jhipster.IntegrationTest;
import es.curso.jhipster.domain.Director;
import es.curso.jhipster.repository.DirectorRepository;
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
 * Integration tests for the {@link DirectorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DirectorResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDOS = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDOS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/directors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDirectorMockMvc;

    private Director director;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Director createEntity(EntityManager em) {
        Director director = new Director().nombre(DEFAULT_NOMBRE).apellidos(DEFAULT_APELLIDOS);
        return director;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Director createUpdatedEntity(EntityManager em) {
        Director director = new Director().nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS);
        return director;
    }

    @BeforeEach
    public void initTest() {
        director = createEntity(em);
    }

    @Test
    @Transactional
    void createDirector() throws Exception {
        int databaseSizeBeforeCreate = directorRepository.findAll().size();
        // Create the Director
        restDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(director)))
            .andExpect(status().isCreated());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeCreate + 1);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testDirector.getApellidos()).isEqualTo(DEFAULT_APELLIDOS);
    }

    @Test
    @Transactional
    void createDirectorWithExistingId() throws Exception {
        // Create the Director with an existing ID
        director.setId(1L);

        int databaseSizeBeforeCreate = directorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(director)))
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDirectors() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        // Get all the directorList
        restDirectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(director.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellidos").value(hasItem(DEFAULT_APELLIDOS)));
    }

    @Test
    @Transactional
    void getDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        // Get the director
        restDirectorMockMvc
            .perform(get(ENTITY_API_URL_ID, director.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(director.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellidos").value(DEFAULT_APELLIDOS));
    }

    @Test
    @Transactional
    void getNonExistingDirector() throws Exception {
        // Get the director
        restDirectorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        int databaseSizeBeforeUpdate = directorRepository.findAll().size();

        // Update the director
        Director updatedDirector = directorRepository.findById(director.getId()).get();
        // Disconnect from session so that the updates on updatedDirector are not directly saved in db
        em.detach(updatedDirector);
        updatedDirector.nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS);

        restDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDirector.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDirector))
            )
            .andExpect(status().isOk());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDirector.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
    }

    @Test
    @Transactional
    void putNonExistingDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, director.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(director))
            )
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(director))
            )
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(director)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDirectorWithPatch() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        int databaseSizeBeforeUpdate = directorRepository.findAll().size();

        // Update the director using partial update
        Director partialUpdatedDirector = new Director();
        partialUpdatedDirector.setId(director.getId());

        partialUpdatedDirector.nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS);

        restDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirector))
            )
            .andExpect(status().isOk());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDirector.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
    }

    @Test
    @Transactional
    void fullUpdateDirectorWithPatch() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        int databaseSizeBeforeUpdate = directorRepository.findAll().size();

        // Update the director using partial update
        Director partialUpdatedDirector = new Director();
        partialUpdatedDirector.setId(director.getId());

        partialUpdatedDirector.nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS);

        restDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirector))
            )
            .andExpect(status().isOk());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDirector.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
    }

    @Test
    @Transactional
    void patchNonExistingDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, director.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(director))
            )
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(director))
            )
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(director)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        int databaseSizeBeforeDelete = directorRepository.findAll().size();

        // Delete the director
        restDirectorMockMvc
            .perform(delete(ENTITY_API_URL_ID, director.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
