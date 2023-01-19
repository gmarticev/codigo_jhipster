import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEstreno } from '../estreno.model';
import { EstrenoService } from '../service/estreno.service';

@Injectable({ providedIn: 'root' })
export class EstrenoRoutingResolveService implements Resolve<IEstreno | null> {
  constructor(protected service: EstrenoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEstreno | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((estreno: HttpResponse<IEstreno>) => {
          if (estreno.body) {
            return of(estreno.body);
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
