import dayjs from 'dayjs/esm';
import { IPelicula } from 'app/entities/pelicula/pelicula.model';

export interface IActor {
  id: number;
  nombre?: string | null;
  apellidos?: string | null;
  fechaNacimiento?: dayjs.Dayjs | null;
  peliculas?: Pick<IPelicula, 'id'>[] | null;
}

export type NewActor = Omit<IActor, 'id'> & { id: null };
