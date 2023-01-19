export interface IDirector {
  id: number;
  nombre?: string | null;
  apellidos?: string | null;
}

export type NewDirector = Omit<IDirector, 'id'> & { id: null };
