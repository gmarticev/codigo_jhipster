package es.curso.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.curso.jhipster.IntegrationTest;
import es.curso.jhipster.domain.Estreno;
import es.curso.jhipster.repository.EstrenoRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EstrenoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EstrenoResourceIT {

    private static final Instant DEFAULT_FECHA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LUGAR = "AAAAAAAAAA";
    private static final String UPDATED_LUGAR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/estrenos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EstrenoRepository estrenoRepository;

    @Mock
    private EstrenoRepository estrenoRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEstrenoMockMvc;

    private Estreno estreno;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Estreno createEntity(EntityManager em) {
        Estreno estreno = new Estreno().fecha(DEFAULT_FECHA).lugar(DEFAULT_LUGAR);
        return estreno;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Estreno createUpdatedEntity(EntityManager em) {
        Estreno estreno = new Estreno().fecha(UPDATED_FECHA).lugar(UPDATED_LUGAR);
        return estreno;
    }

    @BeforeEach
    public void initTest() {
        estreno = createEntity(em);
    }

    @Test
    @Transactional
    void createEstreno() throws Exception {
        int databaseSizeBeforeCreate = estrenoRepository.findAll().size();
        // Create the Estreno
        restEstrenoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estreno)))
            .andExpect(status().isCreated());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeCreate + 1);
        Estreno testEstreno = estrenoList.get(estrenoList.size() - 1);
        assertThat(testEstreno.getFecha()).isEqualTo(DEFAULT_FECHA);
        assertThat(testEstreno.getLugar()).isEqualTo(DEFAULT_LUGAR);
    }

    @Test
    @Transactional
    void createEstrenoWithExistingId() throws Exception {
        // Create the Estreno with an existing ID
        estreno.setId(1L);

        int databaseSizeBeforeCreate = estrenoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEstrenoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estreno)))
            .andExpect(status().isBadRequest());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEstrenos() throws Exception {
        // Initialize the database
        estrenoRepository.saveAndFlush(estreno);

        // Get all the estrenoList
        restEstrenoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(estreno.getId().intValue())))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())))
            .andExpect(jsonPath("$.[*].lugar").value(hasItem(DEFAULT_LUGAR)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEstrenosWithEagerRelationshipsIsEnabled() throws Exception {
        when(estrenoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEstrenoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(estrenoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEstrenosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(estrenoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEstrenoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(estrenoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEstreno() throws Exception {
        // Initialize the database
        estrenoRepository.saveAndFlush(estreno);

        // Get the estreno
        restEstrenoMockMvc
            .perform(get(ENTITY_API_URL_ID, estreno.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(estreno.getId().intValue()))
            .andExpect(jsonPath("$.fecha").value(DEFAULT_FECHA.toString()))
            .andExpect(jsonPath("$.lugar").value(DEFAULT_LUGAR));
    }

    @Test
    @Transactional
    void getNonExistingEstreno() throws Exception {
        // Get the estreno
        restEstrenoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEstreno() throws Exception {
        // Initialize the database
        estrenoRepository.saveAndFlush(estreno);

        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();

        // Update the estreno
        Estreno updatedEstreno = estrenoRepository.findById(estreno.getId()).get();
        // Disconnect from session so that the updates on updatedEstreno are not directly saved in db
        em.detach(updatedEstreno);
        updatedEstreno.fecha(UPDATED_FECHA).lugar(UPDATED_LUGAR);

        restEstrenoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEstreno.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEstreno))
            )
            .andExpect(status().isOk());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
        Estreno testEstreno = estrenoList.get(estrenoList.size() - 1);
        assertThat(testEstreno.getFecha()).isEqualTo(UPDATED_FECHA);
        assertThat(testEstreno.getLugar()).isEqualTo(UPDATED_LUGAR);
    }

    @Test
    @Transactional
    void putNonExistingEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();
        estreno.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEstrenoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, estreno.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(estreno))
            )
            .andExpect(status().isBadRequest());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();
        estreno.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstrenoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(estreno))
            )
            .andExpect(status().isBadRequest());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();
        estreno.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstrenoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estreno)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEstrenoWithPatch() throws Exception {
        // Initialize the database
        estrenoRepository.saveAndFlush(estreno);

        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();

        // Update the estreno using partial update
        Estreno partialUpdatedEstreno = new Estreno();
        partialUpdatedEstreno.setId(estreno.getId());

        restEstrenoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEstreno.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEstreno))
            )
            .andExpect(status().isOk());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
        Estreno testEstreno = estrenoList.get(estrenoList.size() - 1);
        assertThat(testEstreno.getFecha()).isEqualTo(DEFAULT_FECHA);
        assertThat(testEstreno.getLugar()).isEqualTo(DEFAULT_LUGAR);
    }

    @Test
    @Transactional
    void fullUpdateEstrenoWithPatch() throws Exception {
        // Initialize the database
        estrenoRepository.saveAndFlush(estreno);

        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();

        // Update the estreno using partial update
        Estreno partialUpdatedEstreno = new Estreno();
        partialUpdatedEstreno.setId(estreno.getId());

        partialUpdatedEstreno.fecha(UPDATED_FECHA).lugar(UPDATED_LUGAR);

        restEstrenoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEstreno.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEstreno))
            )
            .andExpect(status().isOk());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
        Estreno testEstreno = estrenoList.get(estrenoList.size() - 1);
        assertThat(testEstreno.getFecha()).isEqualTo(UPDATED_FECHA);
        assertThat(testEstreno.getLugar()).isEqualTo(UPDATED_LUGAR);
    }

    @Test
    @Transactional
    void patchNonExistingEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();
        estreno.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEstrenoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, estreno.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(estreno))
            )
            .andExpect(status().isBadRequest());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();
        estreno.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstrenoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(estreno))
            )
            .andExpect(status().isBadRequest());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEstreno() throws Exception {
        int databaseSizeBeforeUpdate = estrenoRepository.findAll().size();
        estreno.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstrenoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(estreno)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Estreno in the database
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEstreno() throws Exception {
        // Initialize the database
        estrenoRepository.saveAndFlush(estreno);

        int databaseSizeBeforeDelete = estrenoRepository.findAll().size();

        // Delete the estreno
        restEstrenoMockMvc
            .perform(delete(ENTITY_API_URL_ID, estreno.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Estreno> estrenoList = estrenoRepository.findAll();
        assertThat(estrenoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
