package es.curso.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.curso.jhipster.IntegrationTest;
import es.curso.jhipster.domain.Actor;
import es.curso.jhipster.domain.Cine;
import es.curso.jhipster.domain.Director;
import es.curso.jhipster.domain.Estreno;
import es.curso.jhipster.domain.Pelicula;
import es.curso.jhipster.repository.PeliculaRepository;
import es.curso.jhipster.service.PeliculaService;
import es.curso.jhipster.service.criteria.PeliculaCriteria;
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
 * Integration tests for the {@link PeliculaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PeliculaResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_ESTRENO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_ESTRENO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBBBBBBBBBBBB";

    private static final Boolean DEFAULT_EN_CINES = false;
    private static final Boolean UPDATED_EN_CINES = true;

    private static final String ENTITY_API_URL = "/api/peliculas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Mock
    private PeliculaRepository peliculaRepositoryMock;

    @Mock
    private PeliculaService peliculaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPeliculaMockMvc;

    private Pelicula pelicula;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pelicula createEntity(EntityManager em) {
        Pelicula pelicula = new Pelicula()
            .titulo(DEFAULT_TITULO)
            .fechaEstreno(DEFAULT_FECHA_ESTRENO)
            .descripcion(DEFAULT_DESCRIPCION)
            .enCines(DEFAULT_EN_CINES);
        return pelicula;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pelicula createUpdatedEntity(EntityManager em) {
        Pelicula pelicula = new Pelicula()
            .titulo(UPDATED_TITULO)
            .fechaEstreno(UPDATED_FECHA_ESTRENO)
            .descripcion(UPDATED_DESCRIPCION)
            .enCines(UPDATED_EN_CINES);
        return pelicula;
    }

    @BeforeEach
    public void initTest() {
        pelicula = createEntity(em);
    }

    @Test
    @Transactional
    void createPelicula() throws Exception {
        int databaseSizeBeforeCreate = peliculaRepository.findAll().size();
        // Create the Pelicula
        restPeliculaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pelicula)))
            .andExpect(status().isCreated());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeCreate + 1);
        Pelicula testPelicula = peliculaList.get(peliculaList.size() - 1);
        assertThat(testPelicula.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testPelicula.getFechaEstreno()).isEqualTo(DEFAULT_FECHA_ESTRENO);
        assertThat(testPelicula.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testPelicula.getEnCines()).isEqualTo(DEFAULT_EN_CINES);
    }

    @Test
    @Transactional
    void createPeliculaWithExistingId() throws Exception {
        // Create the Pelicula with an existing ID
        pelicula.setId(1L);

        int databaseSizeBeforeCreate = peliculaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPeliculaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pelicula)))
            .andExpect(status().isBadRequest());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTituloIsRequired() throws Exception {
        int databaseSizeBeforeTest = peliculaRepository.findAll().size();
        // set the field null
        pelicula.setTitulo(null);

        // Create the Pelicula, which fails.

        restPeliculaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pelicula)))
            .andExpect(status().isBadRequest());

        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPeliculas() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList
        restPeliculaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pelicula.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].fechaEstreno").value(hasItem(DEFAULT_FECHA_ESTRENO.toString())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].enCines").value(hasItem(DEFAULT_EN_CINES.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPeliculasWithEagerRelationshipsIsEnabled() throws Exception {
        when(peliculaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPeliculaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(peliculaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPeliculasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(peliculaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPeliculaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(peliculaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPelicula() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get the pelicula
        restPeliculaMockMvc
            .perform(get(ENTITY_API_URL_ID, pelicula.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pelicula.getId().intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.fechaEstreno").value(DEFAULT_FECHA_ESTRENO.toString()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.enCines").value(DEFAULT_EN_CINES.booleanValue()));
    }

    @Test
    @Transactional
    void getPeliculasByIdFiltering() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        Long id = pelicula.getId();

        defaultPeliculaShouldBeFound("id.equals=" + id);
        defaultPeliculaShouldNotBeFound("id.notEquals=" + id);

        defaultPeliculaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPeliculaShouldNotBeFound("id.greaterThan=" + id);

        defaultPeliculaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPeliculaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPeliculasByTituloIsEqualToSomething() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where titulo equals to DEFAULT_TITULO
        defaultPeliculaShouldBeFound("titulo.equals=" + DEFAULT_TITULO);

        // Get all the peliculaList where titulo equals to UPDATED_TITULO
        defaultPeliculaShouldNotBeFound("titulo.equals=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllPeliculasByTituloIsInShouldWork() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where titulo in DEFAULT_TITULO or UPDATED_TITULO
        defaultPeliculaShouldBeFound("titulo.in=" + DEFAULT_TITULO + "," + UPDATED_TITULO);

        // Get all the peliculaList where titulo equals to UPDATED_TITULO
        defaultPeliculaShouldNotBeFound("titulo.in=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllPeliculasByTituloIsNullOrNotNull() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where titulo is not null
        defaultPeliculaShouldBeFound("titulo.specified=true");

        // Get all the peliculaList where titulo is null
        defaultPeliculaShouldNotBeFound("titulo.specified=false");
    }

    @Test
    @Transactional
    void getAllPeliculasByTituloContainsSomething() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where titulo contains DEFAULT_TITULO
        defaultPeliculaShouldBeFound("titulo.contains=" + DEFAULT_TITULO);

        // Get all the peliculaList where titulo contains UPDATED_TITULO
        defaultPeliculaShouldNotBeFound("titulo.contains=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllPeliculasByTituloNotContainsSomething() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where titulo does not contain DEFAULT_TITULO
        defaultPeliculaShouldNotBeFound("titulo.doesNotContain=" + DEFAULT_TITULO);

        // Get all the peliculaList where titulo does not contain UPDATED_TITULO
        defaultPeliculaShouldBeFound("titulo.doesNotContain=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllPeliculasByFechaEstrenoIsEqualToSomething() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where fechaEstreno equals to DEFAULT_FECHA_ESTRENO
        defaultPeliculaShouldBeFound("fechaEstreno.equals=" + DEFAULT_FECHA_ESTRENO);

        // Get all the peliculaList where fechaEstreno equals to UPDATED_FECHA_ESTRENO
        defaultPeliculaShouldNotBeFound("fechaEstreno.equals=" + UPDATED_FECHA_ESTRENO);
    }

    @Test
    @Transactional
    void getAllPeliculasByFechaEstrenoIsInShouldWork() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where fechaEstreno in DEFAULT_FECHA_ESTRENO or UPDATED_FECHA_ESTRENO
        defaultPeliculaShouldBeFound("fechaEstreno.in=" + DEFAULT_FECHA_ESTRENO + "," + UPDATED_FECHA_ESTRENO);

        // Get all the peliculaList where fechaEstreno equals to UPDATED_FECHA_ESTRENO
        defaultPeliculaShouldNotBeFound("fechaEstreno.in=" + UPDATED_FECHA_ESTRENO);
    }

    @Test
    @Transactional
    void getAllPeliculasByFechaEstrenoIsNullOrNotNull() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where fechaEstreno is not null
        defaultPeliculaShouldBeFound("fechaEstreno.specified=true");

        // Get all the peliculaList where fechaEstreno is null
        defaultPeliculaShouldNotBeFound("fechaEstreno.specified=false");
    }

    @Test
    @Transactional
    void getAllPeliculasByDescripcionIsEqualToSomething() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where descripcion equals to DEFAULT_DESCRIPCION
        defaultPeliculaShouldBeFound("descripcion.equals=" + DEFAULT_DESCRIPCION);

        // Get all the peliculaList where descripcion equals to UPDATED_DESCRIPCION
        defaultPeliculaShouldNotBeFound("descripcion.equals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllPeliculasByDescripcionIsInShouldWork() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where descripcion in DEFAULT_DESCRIPCION or UPDATED_DESCRIPCION
        defaultPeliculaShouldBeFound("descripcion.in=" + DEFAULT_DESCRIPCION + "," + UPDATED_DESCRIPCION);

        // Get all the peliculaList where descripcion equals to UPDATED_DESCRIPCION
        defaultPeliculaShouldNotBeFound("descripcion.in=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllPeliculasByDescripcionIsNullOrNotNull() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where descripcion is not null
        defaultPeliculaShouldBeFound("descripcion.specified=true");

        // Get all the peliculaList where descripcion is null
        defaultPeliculaShouldNotBeFound("descripcion.specified=false");
    }

    @Test
    @Transactional
    void getAllPeliculasByDescripcionContainsSomething() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where descripcion contains DEFAULT_DESCRIPCION
        defaultPeliculaShouldBeFound("descripcion.contains=" + DEFAULT_DESCRIPCION);

        // Get all the peliculaList where descripcion contains UPDATED_DESCRIPCION
        defaultPeliculaShouldNotBeFound("descripcion.contains=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllPeliculasByDescripcionNotContainsSomething() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where descripcion does not contain DEFAULT_DESCRIPCION
        defaultPeliculaShouldNotBeFound("descripcion.doesNotContain=" + DEFAULT_DESCRIPCION);

        // Get all the peliculaList where descripcion does not contain UPDATED_DESCRIPCION
        defaultPeliculaShouldBeFound("descripcion.doesNotContain=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllPeliculasByEnCinesIsEqualToSomething() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where enCines equals to DEFAULT_EN_CINES
        defaultPeliculaShouldBeFound("enCines.equals=" + DEFAULT_EN_CINES);

        // Get all the peliculaList where enCines equals to UPDATED_EN_CINES
        defaultPeliculaShouldNotBeFound("enCines.equals=" + UPDATED_EN_CINES);
    }

    @Test
    @Transactional
    void getAllPeliculasByEnCinesIsInShouldWork() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where enCines in DEFAULT_EN_CINES or UPDATED_EN_CINES
        defaultPeliculaShouldBeFound("enCines.in=" + DEFAULT_EN_CINES + "," + UPDATED_EN_CINES);

        // Get all the peliculaList where enCines equals to UPDATED_EN_CINES
        defaultPeliculaShouldNotBeFound("enCines.in=" + UPDATED_EN_CINES);
    }

    @Test
    @Transactional
    void getAllPeliculasByEnCinesIsNullOrNotNull() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        // Get all the peliculaList where enCines is not null
        defaultPeliculaShouldBeFound("enCines.specified=true");

        // Get all the peliculaList where enCines is null
        defaultPeliculaShouldNotBeFound("enCines.specified=false");
    }

    @Test
    @Transactional
    void getAllPeliculasByEstrenoIsEqualToSomething() throws Exception {
        Estreno estreno;
        if (TestUtil.findAll(em, Estreno.class).isEmpty()) {
            peliculaRepository.saveAndFlush(pelicula);
            estreno = EstrenoResourceIT.createEntity(em);
        } else {
            estreno = TestUtil.findAll(em, Estreno.class).get(0);
        }
        em.persist(estreno);
        em.flush();
        pelicula.setEstreno(estreno);
        estreno.setPelicula(pelicula);
        peliculaRepository.saveAndFlush(pelicula);
        Long estrenoId = estreno.getId();

        // Get all the peliculaList where estreno equals to estrenoId
        defaultPeliculaShouldBeFound("estrenoId.equals=" + estrenoId);

        // Get all the peliculaList where estreno equals to (estrenoId + 1)
        defaultPeliculaShouldNotBeFound("estrenoId.equals=" + (estrenoId + 1));
    }

    @Test
    @Transactional
    void getAllPeliculasByDirectorIsEqualToSomething() throws Exception {
        Director director;
        if (TestUtil.findAll(em, Director.class).isEmpty()) {
            peliculaRepository.saveAndFlush(pelicula);
            director = DirectorResourceIT.createEntity(em);
        } else {
            director = TestUtil.findAll(em, Director.class).get(0);
        }
        em.persist(director);
        em.flush();
        pelicula.setDirector(director);
        peliculaRepository.saveAndFlush(pelicula);
        Long directorId = director.getId();

        // Get all the peliculaList where director equals to directorId
        defaultPeliculaShouldBeFound("directorId.equals=" + directorId);

        // Get all the peliculaList where director equals to (directorId + 1)
        defaultPeliculaShouldNotBeFound("directorId.equals=" + (directorId + 1));
    }

    @Test
    @Transactional
    void getAllPeliculasByActorIsEqualToSomething() throws Exception {
        Actor actor;
        if (TestUtil.findAll(em, Actor.class).isEmpty()) {
            peliculaRepository.saveAndFlush(pelicula);
            actor = ActorResourceIT.createEntity(em);
        } else {
            actor = TestUtil.findAll(em, Actor.class).get(0);
        }
        em.persist(actor);
        em.flush();
        pelicula.addActor(actor);
        peliculaRepository.saveAndFlush(pelicula);
        Long actorId = actor.getId();

        // Get all the peliculaList where actor equals to actorId
        defaultPeliculaShouldBeFound("actorId.equals=" + actorId);

        // Get all the peliculaList where actor equals to (actorId + 1)
        defaultPeliculaShouldNotBeFound("actorId.equals=" + (actorId + 1));
    }

    @Test
    @Transactional
    void getAllPeliculasByCineIsEqualToSomething() throws Exception {
        Cine cine;
        if (TestUtil.findAll(em, Cine.class).isEmpty()) {
            peliculaRepository.saveAndFlush(pelicula);
            cine = CineResourceIT.createEntity(em);
        } else {
            cine = TestUtil.findAll(em, Cine.class).get(0);
        }
        em.persist(cine);
        em.flush();
        peliculaRepository.saveAndFlush(pelicula);
        Long cineId = cine.getId();

        // Get all the peliculaList where cine equals to cineId
        defaultPeliculaShouldBeFound("cineId.equals=" + cineId);

        // Get all the peliculaList where cine equals to (cineId + 1)
        defaultPeliculaShouldNotBeFound("cineId.equals=" + (cineId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPeliculaShouldBeFound(String filter) throws Exception {
        restPeliculaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pelicula.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].fechaEstreno").value(hasItem(DEFAULT_FECHA_ESTRENO.toString())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].enCines").value(hasItem(DEFAULT_EN_CINES.booleanValue())));

        // Check, that the count call also returns 1
        restPeliculaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPeliculaShouldNotBeFound(String filter) throws Exception {
        restPeliculaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPeliculaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPelicula() throws Exception {
        // Get the pelicula
        restPeliculaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPelicula() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();

        // Update the pelicula
        Pelicula updatedPelicula = peliculaRepository.findById(pelicula.getId()).get();
        // Disconnect from session so that the updates on updatedPelicula are not directly saved in db
        em.detach(updatedPelicula);
        updatedPelicula
            .titulo(UPDATED_TITULO)
            .fechaEstreno(UPDATED_FECHA_ESTRENO)
            .descripcion(UPDATED_DESCRIPCION)
            .enCines(UPDATED_EN_CINES);

        restPeliculaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPelicula.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPelicula))
            )
            .andExpect(status().isOk());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
        Pelicula testPelicula = peliculaList.get(peliculaList.size() - 1);
        assertThat(testPelicula.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testPelicula.getFechaEstreno()).isEqualTo(UPDATED_FECHA_ESTRENO);
        assertThat(testPelicula.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testPelicula.getEnCines()).isEqualTo(UPDATED_EN_CINES);
    }

    @Test
    @Transactional
    void putNonExistingPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();
        pelicula.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPeliculaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pelicula.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pelicula))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();
        pelicula.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPeliculaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pelicula))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();
        pelicula.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPeliculaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pelicula)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePeliculaWithPatch() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();

        // Update the pelicula using partial update
        Pelicula partialUpdatedPelicula = new Pelicula();
        partialUpdatedPelicula.setId(pelicula.getId());

        partialUpdatedPelicula.enCines(UPDATED_EN_CINES);

        restPeliculaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPelicula.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPelicula))
            )
            .andExpect(status().isOk());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
        Pelicula testPelicula = peliculaList.get(peliculaList.size() - 1);
        assertThat(testPelicula.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testPelicula.getFechaEstreno()).isEqualTo(DEFAULT_FECHA_ESTRENO);
        assertThat(testPelicula.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testPelicula.getEnCines()).isEqualTo(UPDATED_EN_CINES);
    }

    @Test
    @Transactional
    void fullUpdatePeliculaWithPatch() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();

        // Update the pelicula using partial update
        Pelicula partialUpdatedPelicula = new Pelicula();
        partialUpdatedPelicula.setId(pelicula.getId());

        partialUpdatedPelicula
            .titulo(UPDATED_TITULO)
            .fechaEstreno(UPDATED_FECHA_ESTRENO)
            .descripcion(UPDATED_DESCRIPCION)
            .enCines(UPDATED_EN_CINES);

        restPeliculaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPelicula.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPelicula))
            )
            .andExpect(status().isOk());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
        Pelicula testPelicula = peliculaList.get(peliculaList.size() - 1);
        assertThat(testPelicula.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testPelicula.getFechaEstreno()).isEqualTo(UPDATED_FECHA_ESTRENO);
        assertThat(testPelicula.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testPelicula.getEnCines()).isEqualTo(UPDATED_EN_CINES);
    }

    @Test
    @Transactional
    void patchNonExistingPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();
        pelicula.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPeliculaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pelicula.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pelicula))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();
        pelicula.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPeliculaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pelicula))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPelicula() throws Exception {
        int databaseSizeBeforeUpdate = peliculaRepository.findAll().size();
        pelicula.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPeliculaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pelicula)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pelicula in the database
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePelicula() throws Exception {
        // Initialize the database
        peliculaRepository.saveAndFlush(pelicula);

        int databaseSizeBeforeDelete = peliculaRepository.findAll().size();

        // Delete the pelicula
        restPeliculaMockMvc
            .perform(delete(ENTITY_API_URL_ID, pelicula.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pelicula> peliculaList = peliculaRepository.findAll();
        assertThat(peliculaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
