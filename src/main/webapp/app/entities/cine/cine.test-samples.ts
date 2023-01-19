import { ICine, NewCine } from './cine.model';

export const sampleWithRequiredData: ICine = {
  id: 84578,
};

export const sampleWithPartialData: ICine = {
  id: 82926,
  nombre: 'Hat Assistant',
  direccion: 'HTTP Fork maroonXXXX',
};

export const sampleWithFullData: ICine = {
  id: 42279,
  nombre: 'withdrawal Corporate',
  direccion: 'facilitateXXXXXXXXXX',
};

export const sampleWithNewData: NewCine = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
