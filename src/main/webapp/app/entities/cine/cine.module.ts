import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CineComponent } from './list/cine.component';
import { CineDetailComponent } from './detail/cine-detail.component';
import { CineUpdateComponent } from './update/cine-update.component';
import { CineDeleteDialogComponent } from './delete/cine-delete-dialog.component';
import { CineRoutingModule } from './route/cine-routing.module';

@NgModule({
  imports: [SharedModule, CineRoutingModule],
  declarations: [CineComponent, CineDetailComponent, CineUpdateComponent, CineDeleteDialogComponent],
})
export class CineModule {}
