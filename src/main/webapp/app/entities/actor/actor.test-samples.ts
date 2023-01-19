import dayjs from 'dayjs/esm';

import { IActor, NewActor } from './actor.model';

export const sampleWithRequiredData: IActor = {
  id: 65808,
};

export const sampleWithPartialData: IActor = {
  id: 8121,
  apellidos: 'proactive Zloty',
  fechaNacimiento: dayjs('2023-01-19T12:59'),
};

export const sampleWithFullData: IActor = {
  id: 91077,
  nombre: 'customized',
  apellidos: 'Saint North Intranet',
  fechaNacimiento: dayjs('2023-01-18T23:46'),
};

export const sampleWithNewData: NewActor = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
