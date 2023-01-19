export interface ICine {
  id: number;
  nombre?: string | null;
  direccion?: string | null;
}

export type NewCine = Omit<ICine, 'id'> & { id: null };
