import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EstrenoFormService, EstrenoFormGroup } from './estreno-form.service';
import { IEstreno } from '../estreno.model';
import { EstrenoService } from '../service/estreno.service';
import { IPelicula } from 'app/entities/pelicula/pelicula.model';
import { PeliculaService } from 'app/entities/pelicula/service/pelicula.service';

@Component({
  selector: 'jhi-estreno-update',
  templateUrl: './estreno-update.component.html',
})
export class EstrenoUpdateComponent implements OnInit {
  isSaving = false;
  estreno: IEstreno | null = null;

  peliculasCollection: IPelicula[] = [];

  editForm: EstrenoFormGroup = this.estrenoFormService.createEstrenoFormGroup();

  constructor(
    protected estrenoService: EstrenoService,
    protected estrenoFormService: EstrenoFormService,
    protected peliculaService: PeliculaService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePelicula = (o1: IPelicula | null, o2: IPelicula | null): boolean => this.peliculaService.comparePelicula(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estreno }) => {
      this.estreno = estreno;
      if (estreno) {
        this.updateForm(estreno);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const estreno = this.estrenoFormService.getEstreno(this.editForm);
    if (estreno.id !== null) {
      this.subscribeToSaveResponse(this.estrenoService.update(estreno));
    } else {
      this.subscribeToSaveResponse(this.estrenoService.create(estreno));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEstreno>>): void {
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

  protected updateForm(estreno: IEstreno): void {
    this.estreno = estreno;
    this.estrenoFormService.resetForm(this.editForm, estreno);

    this.peliculasCollection = this.peliculaService.addPeliculaToCollectionIfMissing<IPelicula>(this.peliculasCollection, estreno.pelicula);
  }

  protected loadRelationshipsOptions(): void {
    this.peliculaService
      .query({ 'estrenoId.specified': 'false' })
      .pipe(map((res: HttpResponse<IPelicula[]>) => res.body ?? []))
      .pipe(
        map((peliculas: IPelicula[]) => this.peliculaService.addPeliculaToCollectionIfMissing<IPelicula>(peliculas, this.estreno?.pelicula))
      )
      .subscribe((peliculas: IPelicula[]) => (this.peliculasCollection = peliculas));
  }
}
