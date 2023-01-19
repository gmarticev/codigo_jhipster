import dayjs from 'dayjs/esm';
import { IPelicula } from 'app/entities/pelicula/pelicula.model';

export interface IEstreno {
  id: number;
  fecha?: dayjs.Dayjs | null;
  lugar?: string | null;
  pelicula?: Pick<IPelicula, 'id' | 'titulo'> | null;
}

export type NewEstreno = Omit<IEstreno, 'id'> & { id: null };
