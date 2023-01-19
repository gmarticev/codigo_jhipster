import { IDirector, NewDirector } from './director.model';

export const sampleWithRequiredData: IDirector = {
  id: 55379,
};

export const sampleWithPartialData: IDirector = {
  id: 56388,
  nombre: 'green Checking',
  apellidos: 'Branch Savings',
};

export const sampleWithFullData: IDirector = {
  id: 65609,
  nombre: 'EXE Shoes Unbranded',
  apellidos: 'Directives',
};

export const sampleWithNewData: NewDirector = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
