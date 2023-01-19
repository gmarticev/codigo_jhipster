import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDirector, NewDirector } from '../director.model';

export type PartialUpdateDirector = Partial<IDirector> & Pick<IDirector, 'id'>;

export type EntityResponseType = HttpResponse<IDirector>;
export type EntityArrayResponseType = HttpResponse<IDirector[]>;

@Injectable({ providedIn: 'root' })
export class DirectorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/directors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(director: NewDirector): Observable<EntityResponseType> {
    return this.http.post<IDirector>(this.resourceUrl, director, { observe: 'response' });
  }

  update(director: IDirector): Observable<EntityResponseType> {
    return this.http.put<IDirector>(`${this.resourceUrl}/${this.getDirectorIdentifier(director)}`, director, { observe: 'response' });
  }

  partialUpdate(director: PartialUpdateDirector): Observable<EntityResponseType> {
    return this.http.patch<IDirector>(`${this.resourceUrl}/${this.getDirectorIdentifier(director)}`, director, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDirector>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDirector[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDirectorIdentifier(director: Pick<IDirector, 'id'>): number {
    return director.id;
  }

  compareDirector(o1: Pick<IDirector, 'id'> | null, o2: Pick<IDirector, 'id'> | null): boolean {
    return o1 && o2 ? this.getDirectorIdentifier(o1) === this.getDirectorIdentifier(o2) : o1 === o2;
  }

  addDirectorToCollectionIfMissing<Type extends Pick<IDirector, 'id'>>(
    directorCollection: Type[],
    ...directorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const directors: Type[] = directorsToCheck.filter(isPresent);
    if (directors.length > 0) {
      const directorCollectionIdentifiers = directorCollection.map(directorItem => this.getDirectorIdentifier(directorItem)!);
      const directorsToAdd = directors.filter(directorItem => {
        const directorIdentifier = this.getDirectorIdentifier(directorItem);
        if (directorCollectionIdentifiers.includes(directorIdentifier)) {
          return false;
        }
        directorCollectionIdentifiers.push(directorIdentifier);
        return true;
      });
      return [...directorsToAdd, ...directorCollection];
    }
    return directorCollection;
  }
}
