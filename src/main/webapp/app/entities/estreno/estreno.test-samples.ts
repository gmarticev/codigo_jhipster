import dayjs from 'dayjs/esm';

import { IEstreno, NewEstreno } from './estreno.model';

export const sampleWithRequiredData: IEstreno = {
  id: 7917,
};

export const sampleWithPartialData: IEstreno = {
  id: 69023,
  lugar: 'quantifying Arizona orchid',
};

export const sampleWithFullData: IEstreno = {
  id: 17448,
  fecha: dayjs('2023-01-13T05:46'),
  lugar: 'Texas application',
};

export const sampleWithNewData: NewEstreno = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
