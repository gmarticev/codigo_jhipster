import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { DirectorFormService, DirectorFormGroup } from './director-form.service';
import { IDirector } from '../director.model';
import { DirectorService } from '../service/director.service';

@Component({
  selector: 'jhi-director-update',
  templateUrl: './director-update.component.html',
})
export class DirectorUpdateComponent implements OnInit {
  isSaving = false;
  director: IDirector | null = null;

  editForm: DirectorFormGroup = this.directorFormService.createDirectorFormGroup();

  constructor(
    protected directorService: DirectorService,
    protected directorFormService: DirectorFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ director }) => {
      this.director = director;
      if (director) {
        this.updateForm(director);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const director = this.directorFormService.getDirector(this.editForm);
    if (director.id !== null) {
      this.subscribeToSaveResponse(this.directorService.update(director));
    } else {
      this.subscribeToSaveResponse(this.directorService.create(director));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDirector>>): void {
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

  protected updateForm(director: IDirector): void {
    this.director = director;
    this.directorFormService.resetForm(this.editForm, director);
  }
}
