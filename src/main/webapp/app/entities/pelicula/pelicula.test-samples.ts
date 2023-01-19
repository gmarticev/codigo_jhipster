import dayjs from 'dayjs/esm';

import { IPelicula, NewPelicula } from './pelicula.model';

export const sampleWithRequiredData: IPelicula = {
  id: 35550,
  titulo: 'Directives alarm Square',
};

export const sampleWithPartialData: IPelicula = {
  id: 18591,
  titulo: 'Rwanda',
};

export const sampleWithFullData: IPelicula = {
  id: 3885,
  titulo: 'solution-oriented Chair',
  fechaEstreno: dayjs('2023-01-13T09:57'),
  descripcion: 'hacking BeautyXXXXXX',
  enCines: true,
};

export const sampleWithNewData: NewPelicula = {
  titulo: 'Cambridgeshire Group',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
