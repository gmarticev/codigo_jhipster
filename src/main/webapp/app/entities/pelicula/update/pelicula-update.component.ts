import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PeliculaFormService, PeliculaFormGroup } from './pelicula-form.service';
import { IPelicula } from '../pelicula.model';
import { PeliculaService } from '../service/pelicula.service';
import { IDirector } from 'app/entities/director/director.model';
import { DirectorService } from 'app/entities/director/service/director.service';
import { IActor } from 'app/entities/actor/actor.model';
import { ActorService } from 'app/entities/actor/service/actor.service';

@Component({
  selector: 'jhi-pelicula-update',
  templateUrl: './pelicula-update.component.html',
})
export class PeliculaUpdateComponent implements OnInit {
  isSaving = false;
  pelicula: IPelicula | null = null;

  directorsSharedCollection: IDirector[] = [];
  actorsSharedCollection: IActor[] = [];

  editForm: PeliculaFormGroup = this.peliculaFormService.createPeliculaFormGroup();

  constructor(
    protected peliculaService: PeliculaService,
    protected peliculaFormService: PeliculaFormService,
    protected directorService: DirectorService,
    protected actorService: ActorService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDirector = (o1: IDirector | null, o2: IDirector | null): boolean => this.directorService.compareDirector(o1, o2);

  compareActor = (o1: IActor | null, o2: IActor | null): boolean => this.actorService.compareActor(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pelicula }) => {
      this.pelicula = pelicula;
      if (pelicula) {
        this.updateForm(pelicula);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pelicula = this.peliculaFormService.getPelicula(this.editForm);
    if (pelicula.id !== null) {
      this.subscribeToSaveResponse(this.peliculaService.update(pelicula));
    } else {
      this.subscribeToSaveResponse(this.peliculaService.create(pelicula));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPelicula>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(pelicula: IPelicula): void {
    this.pelicula = pelicula;
    this.peliculaFormService.resetForm(this.editForm, pelicula);

    this.directorsSharedCollection = this.directorService.addDirectorToCollectionIfMissing<IDirector>(
      this.directorsSharedCollection,
      pelicula.director
    );
    this.actorsSharedCollection = this.actorService.addActorToCollectionIfMissing<IActor>(
      this.actorsSharedCollection,
      ...(pelicula.actors ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.directorService
      .query()
      .pipe(map((res: HttpResponse<IDirector[]>) => res.body ?? []))
      .pipe(
        map((directors: IDirector[]) =>
          this.directorService.addDirectorToCollectionIfMissing<IDirector>(directors, this.pelicula?.director)
        )
      )
      .subscribe((directors: IDirector[]) => (this.directorsSharedCollection = directors));

    this.actorService
      .query()
      .pipe(map((res: HttpResponse<IActor[]>) => res.body ?? []))
      .pipe(map((actors: IActor[]) => this.actorService.addActorToCollectionIfMissing<IActor>(actors, ...(this.pelicula?.actors ?? []))))
      .subscribe((actors: IActor[]) => (this.actorsSharedCollection = actors));
  }
}
