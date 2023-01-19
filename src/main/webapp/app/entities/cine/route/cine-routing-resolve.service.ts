import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICine } from '../cine.model';
import { CineService } from '../service/cine.service';

@Injectable({ providedIn: 'root' })
export class CineRoutingResolveService implements Resolve<ICine | null> {
  constructor(protected service: CineService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICine | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cine: HttpResponse<ICine>) => {
          if (cine.body) {
            return of(cine.body);
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
