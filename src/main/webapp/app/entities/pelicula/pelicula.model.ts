import dayjs from 'dayjs/esm';
import { IDirector } from 'app/entities/director/director.model';
import { IActor } from 'app/entities/actor/actor.model';

export interface IPelicula {
  id: number;
  titulo?: string | null;
  fechaEstreno?: dayjs.Dayjs | null;
  descripcion?: string | null;
  enCines?: boolean | null;
  director?: Pick<IDirector, 'id' | 'nombre'> | null;
  actors?: Pick<IActor, 'id' | 'nombre'>[] | null;
}

export type NewPelicula = Omit<IPelicula, 'id'> & { id: null };
