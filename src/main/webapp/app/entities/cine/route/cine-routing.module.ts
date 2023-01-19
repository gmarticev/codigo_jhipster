import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CineComponent } from '../list/cine.component';
import { CineDetailComponent } from '../detail/cine-detail.component';
import { CineUpdateComponent } from '../update/cine-update.component';
import { CineRoutingResolveService } from './cine-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const cineRoute: Routes = [
  {
    path: '',
    component: CineComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CineDetailComponent,
    resolve: {
      cine: CineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CineUpdateComponent,
    resolve: {
      cine: CineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CineUpdateComponent,
    resolve: {
      cine: CineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cineRoute)],
  exports: [RouterModule],
})
export class CineRoutingModule {}
