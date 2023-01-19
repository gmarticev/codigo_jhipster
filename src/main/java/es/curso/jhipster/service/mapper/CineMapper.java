package es.curso.jhipster.service.mapper;

import es.curso.jhipster.domain.Cine;
import es.curso.jhipster.service.dto.CineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cine} and its DTO {@link CineDTO}.
 */
@Mapper(componentModel = "spring")
public interface CineMapper extends EntityMapper<CineDTO, Cine> {}
