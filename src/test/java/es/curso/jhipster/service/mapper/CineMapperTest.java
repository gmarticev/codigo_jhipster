package es.curso.jhipster.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CineMapperTest {

    private CineMapper cineMapper;

    @BeforeEach
    public void setUp() {
        cineMapper = new CineMapperImpl();
    }
}
