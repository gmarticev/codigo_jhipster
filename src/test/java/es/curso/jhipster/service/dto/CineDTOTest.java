package es.curso.jhipster.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import es.curso.jhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CineDTO.class);
        CineDTO cineDTO1 = new CineDTO();
        cineDTO1.setId(1L);
        CineDTO cineDTO2 = new CineDTO();
        assertThat(cineDTO1).isNotEqualTo(cineDTO2);
        cineDTO2.setId(cineDTO1.getId());
        assertThat(cineDTO1).isEqualTo(cineDTO2);
        cineDTO2.setId(2L);
        assertThat(cineDTO1).isNotEqualTo(cineDTO2);
        cineDTO1.setId(null);
        assertThat(cineDTO1).isNotEqualTo(cineDTO2);
    }
}
