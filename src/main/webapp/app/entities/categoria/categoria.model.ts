export interface ICategoria {
  id: number;
  nombre?: string | null;
  imagen?: string | null;
  imagenContentType?: string | null;
}

export type NewCategoria = Omit<ICategoria, 'id'> & { id: null };
