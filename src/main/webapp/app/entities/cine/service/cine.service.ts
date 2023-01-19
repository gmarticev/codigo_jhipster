import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICine, NewCine } from '../cine.model';

export type PartialUpdateCine = Partial<ICine> & Pick<ICine, 'id'>;

export type EntityResponseType = HttpResponse<ICine>;
export type EntityArrayResponseType = HttpResponse<ICine[]>;

@Injectable({ providedIn: 'root' })
export class CineService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cines');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cine: NewCine): Observable<EntityResponseType> {
    return this.http.post<ICine>(this.resourceUrl, cine, { observe: 'response' });
  }

  update(cine: ICine): Observable<EntityResponseType> {
    return this.http.put<ICine>(`${this.resourceUrl}/${this.getCineIdentifier(cine)}`, cine, { observe: 'response' });
  }

  partialUpdate(cine: PartialUpdateCine): Observable<EntityResponseType> {
    return this.http.patch<ICine>(`${this.resourceUrl}/${this.getCineIdentifier(cine)}`, cine, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICine>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICine[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCineIdentifier(cine: Pick<ICine, 'id'>): number {
    return cine.id;
  }

  compareCine(o1: Pick<ICine, 'id'> | null, o2: Pick<ICine, 'id'> | null): boolean {
    return o1 && o2 ? this.getCineIdentifier(o1) === this.getCineIdentifier(o2) : o1 === o2;
  }

  addCineToCollectionIfMissing<Type extends Pick<ICine, 'id'>>(
    cineCollection: Type[],
    ...cinesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cines: Type[] = cinesToCheck.filter(isPresent);
    if (cines.length > 0) {
      const cineCollectionIdentifiers = cineCollection.map(cineItem => this.getCineIdentifier(cineItem)!);
      const cinesToAdd = cines.filter(cineItem => {
        const cineIdentifier = this.getCineIdentifier(cineItem);
        if (cineCollectionIdentifiers.includes(cineIdentifier)) {
          return false;
        }
        cineCollectionIdentifiers.push(cineIdentifier);
        return true;
      });
      return [...cinesToAdd, ...cineCollection];
    }
    return cineCollection;
  }
}
