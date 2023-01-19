import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPelicula, NewPelicula } from '../pelicula.model';

export type PartialUpdatePelicula = Partial<IPelicula> & Pick<IPelicula, 'id'>;

type RestOf<T extends IPelicula | NewPelicula> = Omit<T, 'fechaEstreno'> & {
  fechaEstreno?: string | null;
};

export type RestPelicula = RestOf<IPelicula>;

export type NewRestPelicula = RestOf<NewPelicula>;

export type PartialUpdateRestPelicula = RestOf<PartialUpdatePelicula>;

export type EntityResponseType = HttpResponse<IPelicula>;
export type EntityArrayResponseType = HttpResponse<IPelicula[]>;

@Injectable({ providedIn: 'root' })
export class PeliculaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/peliculas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pelicula: NewPelicula): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pelicula);
    return this.http
      .post<RestPelicula>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(pelicula: IPelicula): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pelicula);
    return this.http
      .put<RestPelicula>(`${this.resourceUrl}/${this.getPeliculaIdentifier(pelicula)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(pelicula: PartialUpdatePelicula): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pelicula);
    return this.http
      .patch<RestPelicula>(`${this.resourceUrl}/${this.getPeliculaIdentifier(pelicula)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPelicula>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPelicula[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPeliculaIdentifier(pelicula: Pick<IPelicula, 'id'>): number {
    return pelicula.id;
  }

  comparePelicula(o1: Pick<IPelicula, 'id'> | null, o2: Pick<IPelicula, 'id'> | null): boolean {
    return o1 && o2 ? this.getPeliculaIdentifier(o1) === this.getPeliculaIdentifier(o2) : o1 === o2;
  }

  addPeliculaToCollectionIfMissing<Type extends Pick<IPelicula, 'id'>>(
    peliculaCollection: Type[],
    ...peliculasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const peliculas: Type[] = peliculasToCheck.filter(isPresent);
    if (peliculas.length > 0) {
      const peliculaCollectionIdentifiers = peliculaCollection.map(peliculaItem => this.getPeliculaIdentifier(peliculaItem)!);
      const peliculasToAdd = peliculas.filter(peliculaItem => {
        const peliculaIdentifier = this.getPeliculaIdentifier(peliculaItem);
        if (peliculaCollectionIdentifiers.includes(peliculaIdentifier)) {
          return false;
        }
        peliculaCollectionIdentifiers.push(peliculaIdentifier);
        return true;
      });
      return [...peliculasToAdd, ...peliculaCollection];
    }
    return peliculaCollection;
  }

  protected convertDateFromClient<T extends IPelicula | NewPelicula | PartialUpdatePelicula>(pelicula: T): RestOf<T> {
    return {
      ...pelicula,
      fechaEstreno: pelicula.fechaEstreno?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPelicula: RestPelicula): IPelicula {
    return {
      ...restPelicula,
      fechaEstreno: restPelicula.fechaEstreno ? dayjs(restPelicula.fechaEstreno) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPelicula>): HttpResponse<IPelicula> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPelicula[]>): HttpResponse<IPelicula[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
