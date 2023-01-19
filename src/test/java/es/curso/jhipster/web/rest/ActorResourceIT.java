package es.curso.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.curso.jhipster.IntegrationTest;
import es.curso.jhipster.domain.Actor;
import es.curso.jhipster.domain.Pelicula;
import es.curso.jhipster.repository.ActorRepository;
import es.curso.jhipster.service.criteria.ActorCriteria;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ActorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActorResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDOS = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDOS = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_NACIMIENTO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_NACIMIENTO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/actors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActorMockMvc;

    private Actor actor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createEntity(EntityManager em) {
        Actor actor = new Actor().nombre(DEFAULT_NOMBRE).apellidos(DEFAULT_APELLIDOS).fechaNacimiento(DEFAULT_FECHA_NACIMIENTO);
        return actor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createUpdatedEntity(EntityManager em) {
        Actor actor = new Actor().nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);
        return actor;
    }

    @BeforeEach
    public void initTest() {
        actor = createEntity(em);
    }

    @Test
    @Transactional
    void createActor() throws Exception {
        int databaseSizeBeforeCreate = actorRepository.findAll().size();
        // Create the Actor
        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isCreated());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate + 1);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testActor.getApellidos()).isEqualTo(DEFAULT_APELLIDOS);
        assertThat(testActor.getFechaNacimiento()).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void createActorWithExistingId() throws Exception {
        // Create the Actor with an existing ID
        actor.setId(1L);

        int databaseSizeBeforeCreate = actorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllActors() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList
        restActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellidos").value(hasItem(DEFAULT_APELLIDOS)))
            .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString())));
    }

    @Test
    @Transactional
    void getActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get the actor
        restActorMockMvc
            .perform(get(ENTITY_API_URL_ID, actor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actor.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellidos").value(DEFAULT_APELLIDOS))
            .andExpect(jsonPath("$.fechaNacimiento").value(DEFAULT_FECHA_NACIMIENTO.toString()));
    }

    @Test
    @Transactional
    void getActorsByIdFiltering() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        Long id = actor.getId();

        defaultActorShouldBeFound("id.equals=" + id);
        defaultActorShouldNotBeFound("id.notEquals=" + id);

        defaultActorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultActorShouldNotBeFound("id.greaterThan=" + id);

        defaultActorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultActorShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllActorsByNombreIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where nombre equals to DEFAULT_NOMBRE
        defaultActorShouldBeFound("nombre.equals=" + DEFAULT_NOMBRE);

        // Get all the actorList where nombre equals to UPDATED_NOMBRE
        defaultActorShouldNotBeFound("nombre.equals=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllActorsByNombreIsInShouldWork() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where nombre in DEFAULT_NOMBRE or UPDATED_NOMBRE
        defaultActorShouldBeFound("nombre.in=" + DEFAULT_NOMBRE + "," + UPDATED_NOMBRE);

        // Get all the actorList where nombre equals to UPDATED_NOMBRE
        defaultActorShouldNotBeFound("nombre.in=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllActorsByNombreIsNullOrNotNull() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where nombre is not null
        defaultActorShouldBeFound("nombre.specified=true");

        // Get all the actorList where nombre is null
        defaultActorShouldNotBeFound("nombre.specified=false");
    }

    @Test
    @Transactional
    void getAllActorsByNombreContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where nombre contains DEFAULT_NOMBRE
        defaultActorShouldBeFound("nombre.contains=" + DEFAULT_NOMBRE);

        // Get all the actorList where nombre contains UPDATED_NOMBRE
        defaultActorShouldNotBeFound("nombre.contains=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllActorsByNombreNotContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where nombre does not contain DEFAULT_NOMBRE
        defaultActorShouldNotBeFound("nombre.doesNotContain=" + DEFAULT_NOMBRE);

        // Get all the actorList where nombre does not contain UPDATED_NOMBRE
        defaultActorShouldBeFound("nombre.doesNotContain=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllActorsByApellidosIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where apellidos equals to DEFAULT_APELLIDOS
        defaultActorShouldBeFound("apellidos.equals=" + DEFAULT_APELLIDOS);

        // Get all the actorList where apellidos equals to UPDATED_APELLIDOS
        defaultActorShouldNotBeFound("apellidos.equals=" + UPDATED_APELLIDOS);
    }

    @Test
    @Transactional
    void getAllActorsByApellidosIsInShouldWork() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where apellidos in DEFAULT_APELLIDOS or UPDATED_APELLIDOS
        defaultActorShouldBeFound("apellidos.in=" + DEFAULT_APELLIDOS + "," + UPDATED_APELLIDOS);

        // Get all the actorList where apellidos equals to UPDATED_APELLIDOS
        defaultActorShouldNotBeFound("apellidos.in=" + UPDATED_APELLIDOS);
    }

    @Test
    @Transactional
    void getAllActorsByApellidosIsNullOrNotNull() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where apellidos is not null
        defaultActorShouldBeFound("apellidos.specified=true");

        // Get all the actorList where apellidos is null
        defaultActorShouldNotBeFound("apellidos.specified=false");
    }

    @Test
    @Transactional
    void getAllActorsByApellidosContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where apellidos contains DEFAULT_APELLIDOS
        defaultActorShouldBeFound("apellidos.contains=" + DEFAULT_APELLIDOS);

        // Get all the actorList where apellidos contains UPDATED_APELLIDOS
        defaultActorShouldNotBeFound("apellidos.contains=" + UPDATED_APELLIDOS);
    }

    @Test
    @Transactional
    void getAllActorsByApellidosNotContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where apellidos does not contain DEFAULT_APELLIDOS
        defaultActorShouldNotBeFound("apellidos.doesNotContain=" + DEFAULT_APELLIDOS);

        // Get all the actorList where apellidos does not contain UPDATED_APELLIDOS
        defaultActorShouldBeFound("apellidos.doesNotContain=" + UPDATED_APELLIDOS);
    }

    @Test
    @Transactional
    void getAllActorsByFechaNacimientoIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where fechaNacimiento equals to DEFAULT_FECHA_NACIMIENTO
        defaultActorShouldBeFound("fechaNacimiento.equals=" + DEFAULT_FECHA_NACIMIENTO);

        // Get all the actorList where fechaNacimiento equals to UPDATED_FECHA_NACIMIENTO
        defaultActorShouldNotBeFound("fechaNacimiento.equals=" + UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void getAllActorsByFechaNacimientoIsInShouldWork() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where fechaNacimiento in DEFAULT_FECHA_NACIMIENTO or UPDATED_FECHA_NACIMIENTO
        defaultActorShouldBeFound("fechaNacimiento.in=" + DEFAULT_FECHA_NACIMIENTO + "," + UPDATED_FECHA_NACIMIENTO);

        // Get all the actorList where fechaNacimiento equals to UPDATED_FECHA_NACIMIENTO
        defaultActorShouldNotBeFound("fechaNacimiento.in=" + UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void getAllActorsByFechaNacimientoIsNullOrNotNull() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where fechaNacimiento is not null
        defaultActorShouldBeFound("fechaNacimiento.specified=true");

        // Get all the actorList where fechaNacimiento is null
        defaultActorShouldNotBeFound("fechaNacimiento.specified=false");
    }

    @Test
    @Transactional
    void getAllActorsByPeliculaIsEqualToSomething() throws Exception {
        Pelicula pelicula;
        if (TestUtil.findAll(em, Pelicula.class).isEmpty()) {
            actorRepository.saveAndFlush(actor);
            pelicula = PeliculaResourceIT.createEntity(em);
        } else {
            pelicula = TestUtil.findAll(em, Pelicula.class).get(0);
        }
        em.persist(pelicula);
        em.flush();
        actor.addPelicula(pelicula);
        actorRepository.saveAndFlush(actor);
        Long peliculaId = pelicula.getId();

        // Get all the actorList where pelicula equals to peliculaId
        defaultActorShouldBeFound("peliculaId.equals=" + peliculaId);

        // Get all the actorList where pelicula equals to (peliculaId + 1)
        defaultActorShouldNotBeFound("peliculaId.equals=" + (peliculaId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultActorShouldBeFound(String filter) throws Exception {
        restActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellidos").value(hasItem(DEFAULT_APELLIDOS)))
            .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString())));

        // Check, that the count call also returns 1
        restActorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultActorShouldNotBeFound(String filter) throws Exception {
        restActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restActorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingActor() throws Exception {
        // Get the actor
        restActorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor
        Actor updatedActor = actorRepository.findById(actor.getId()).get();
        // Disconnect from session so that the updates on updatedActor are not directly saved in db
        em.detach(updatedActor);
        updatedActor.nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);

        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testActor.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
        assertThat(testActor.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void putNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setId(actor.getId());

        partialUpdatedActor.nombre(UPDATED_NOMBRE).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);

        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testActor.getApellidos()).isEqualTo(DEFAULT_APELLIDOS);
        assertThat(testActor.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void fullUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setId(actor.getId());

        partialUpdatedActor.nombre(UPDATED_NOMBRE).apellidos(UPDATED_APELLIDOS).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);

        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testActor.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
        assertThat(testActor.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void patchNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeDelete = actorRepository.findAll().size();

        // Delete the actor
        restActorMockMvc
            .perform(delete(ENTITY_API_URL_ID, actor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
