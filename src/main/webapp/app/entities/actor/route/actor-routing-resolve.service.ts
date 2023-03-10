import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IActor } from '../actor.model';
import { ActorService } from '../service/actor.service';

@Injectable({ providedIn: 'root' })
export class ActorRoutingResolveService implements Resolve<IActor | null> {
  constructor(protected service: ActorService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IActor | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((actor: HttpResponse<IActor>) => {
          if (actor.body) {
            return of(actor.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
