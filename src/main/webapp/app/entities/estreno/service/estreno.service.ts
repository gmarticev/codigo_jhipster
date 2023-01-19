import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEstreno, NewEstreno } from '../estreno.model';

export type PartialUpdateEstreno = Partial<IEstreno> & Pick<IEstreno, 'id'>;

type RestOf<T extends IEstreno | NewEstreno> = Omit<T, 'fecha'> & {
  fecha?: string | null;
};

export type RestEstreno = RestOf<IEstreno>;

export type NewRestEstreno = RestOf<NewEstreno>;

export type PartialUpdateRestEstreno = RestOf<PartialUpdateEstreno>;

export type EntityResponseType = HttpResponse<IEstreno>;
export type EntityArrayResponseType = HttpResponse<IEstreno[]>;

@Injectable({ providedIn: 'root' })
export class EstrenoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/estrenos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(estreno: NewEstreno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estreno);
    return this.http
      .post<RestEstreno>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(estreno: IEstreno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estreno);
    return this.http
      .put<RestEstreno>(`${this.resourceUrl}/${this.getEstrenoIdentifier(estreno)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(estreno: PartialUpdateEstreno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estreno);
    return this.http
      .patch<RestEstreno>(`${this.resourceUrl}/${this.getEstrenoIdentifier(estreno)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEstreno>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEstreno[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEstrenoIdentifier(estreno: Pick<IEstreno, 'id'>): number {
    return estreno.id;
  }

  compareEstreno(o1: Pick<IEstreno, 'id'> | null, o2: Pick<IEstreno, 'id'> | null): boolean {
    return o1 && o2 ? this.getEstrenoIdentifier(o1) === this.getEstrenoIdentifier(o2) : o1 === o2;
  }

  addEstrenoToCollectionIfMissing<Type extends Pick<IEstreno, 'id'>>(
    estrenoCollection: Type[],
    ...estrenosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const estrenos: Type[] = estrenosToCheck.filter(isPresent);
    if (estrenos.length > 0) {
      const estrenoCollectionIdentifiers = estrenoCollection.map(estrenoItem => this.getEstrenoIdentifier(estrenoItem)!);
      const estrenosToAdd = estrenos.filter(estrenoItem => {
        const estrenoIdentifier = this.getEstrenoIdentifier(estrenoItem);
        if (estrenoCollectionIdentifiers.includes(estrenoIdentifier)) {
          return false;
        }
        estrenoCollectionIdentifiers.push(estrenoIdentifier);
        return true;
      });
      return [...estrenosToAdd, ...estrenoCollection];
    }
    return estrenoCollection;
  }

  protected convertDateFromClient<T extends IEstreno | NewEstreno | PartialUpdateEstreno>(estreno: T): RestOf<T> {
    return {
      ...estreno,
      fecha: estreno.fecha?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEstreno: RestEstreno): IEstreno {
    return {
      ...restEstreno,
      fecha: restEstreno.fecha ? dayjs(restEstreno.fecha) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEstreno>): HttpResponse<IEstreno> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEstreno[]>): HttpResponse<IEstreno[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
