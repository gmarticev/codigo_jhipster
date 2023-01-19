import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CineFormService, CineFormGroup } from './cine-form.service';
import { ICine } from '../cine.model';
import { CineService } from '../service/cine.service';

@Component({
  selector: 'jhi-cine-update',
  templateUrl: './cine-update.component.html',
})
export class CineUpdateComponent implements OnInit {
  isSaving = false;
  cine: ICine | null = null;

  editForm: CineFormGroup = this.cineFormService.createCineFormGroup();

  constructor(protected cineService: CineService, protected cineFormService: CineFormService, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cine }) => {
      this.cine = cine;
      if (cine) {
        this.updateForm(cine);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cine = this.cineFormService.getCine(this.editForm);
    if (cine.id !== null) {
      this.subscribeToSaveResponse(this.cineService.update(cine));
    } else {
      this.subscribeToSaveResponse(this.cineService.create(cine));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICine>>): void {
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

  protected updateForm(cine: ICine): void {
    this.cine = cine;
    this.cineFormService.resetForm(this.editForm, cine);
  }
}
