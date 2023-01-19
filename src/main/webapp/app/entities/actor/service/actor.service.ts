import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IActor, NewActor } from '../actor.model';

export type PartialUpdateActor = Partial<IActor> & Pick<IActor, 'id'>;

type RestOf<T extends IActor | NewActor> = Omit<T, 'fechaNacimiento'> & {
  fechaNacimiento?: string | null;
};

export type RestActor = RestOf<IActor>;

export type NewRestActor = RestOf<NewActor>;

export type PartialUpdateRestActor = RestOf<PartialUpdateActor>;

export type EntityResponseType = HttpResponse<IActor>;
export type EntityArrayResponseType = HttpResponse<IActor[]>;

@Injectable({ providedIn: 'root' })
export class ActorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/actors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(actor: NewActor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(actor);
    return this.http.post<RestActor>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(actor: IActor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(actor);
    return this.http
      .put<RestActor>(`${this.resourceUrl}/${this.getActorIdentifier(actor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(actor: PartialUpdateActor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(actor);
    return this.http
      .patch<RestActor>(`${this.resourceUrl}/${this.getActorIdentifier(actor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestActor>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestActor[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getActorIdentifier(actor: Pick<IActor, 'id'>): number {
    return actor.id;
  }

  compareActor(o1: Pick<IActor, 'id'> | null, o2: Pick<IActor, 'id'> | null): boolean {
    return o1 && o2 ? this.getActorIdentifier(o1) === this.getActorIdentifier(o2) : o1 === o2;
  }

  addActorToCollectionIfMissing<Type extends Pick<IActor, 'id'>>(
    actorCollection: Type[],
    ...actorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const actors: Type[] = actorsToCheck.filter(isPresent);
    if (actors.length > 0) {
      const actorCollectionIdentifiers = actorCollection.map(actorItem => this.getActorIdentifier(actorItem)!);
      const actorsToAdd = actors.filter(actorItem => {
        const actorIdentifier = this.getActorIdentifier(actorItem);
        if (actorCollectionIdentifiers.includes(actorIdentifier)) {
          return false;
        }
        actorCollectionIdentifiers.push(actorIdentifier);
        return true;
      });
      return [...actorsToAdd, ...actorCollection];
    }
    return actorCollection;
  }

  protected convertDateFromClient<T extends IActor | NewActor | PartialUpdateActor>(actor: T): RestOf<T> {
    return {
      ...actor,
      fechaNacimiento: actor.fechaNacimiento?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restActor: RestActor): IActor {
    return {
      ...restActor,
      fechaNacimiento: restActor.fechaNacimiento ? dayjs(restActor.fechaNacimiento) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestActor>): HttpResponse<IActor> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestActor[]>): HttpResponse<IActor[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
