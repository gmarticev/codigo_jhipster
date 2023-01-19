import { ICategoria, NewCategoria } from './categoria.model';

export const sampleWithRequiredData: ICategoria = {
  id: 34642,
  nombre: 'deposit',
};

export const sampleWithPartialData: ICategoria = {
  id: 17708,
  nombre: 'Tactics withdrawal',
};

export const sampleWithFullData: ICategoria = {
  id: 65789,
  nombre: 'Venezuela',
  imagen: '../fake-data/blob/hipster.png',
  imagenContentType: 'unknown',
};

export const sampleWithNewData: NewCategoria = {
  nombre: 'Buckinghamshire ROI',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
