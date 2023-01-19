package es.curso.jhipster.domain;

import static org.assertj.core.api.Assertions.assertThat;

import es.curso.jhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cine.class);
        Cine cine1 = new Cine();
        cine1.setId(1L);
        Cine cine2 = new Cine();
        cine2.setId(cine1.getId());
        assertThat(cine1).isEqualTo(cine2);
        cine2.setId(2L);
        assertThat(cine1).isNotEqualTo(cine2);
        cine1.setId(null);
        assertThat(cine1).isNotEqualTo(cine2);
    }
}
