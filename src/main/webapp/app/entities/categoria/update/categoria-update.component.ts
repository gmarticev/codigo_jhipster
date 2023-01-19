import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CategoriaFormService, CategoriaFormGroup } from './categoria-form.service';
import { ICategoria } from '../categoria.model';
import { CategoriaService } from '../service/categoria.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-categoria-update',
  templateUrl: './categoria-update.component.html',
})
export class CategoriaUpdateComponent implements OnInit {
  isSaving = false;
  categoria: ICategoria | null = null;

  editForm: CategoriaFormGroup = this.categoriaFormService.createCategoriaFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected categoriaService: CategoriaService,
    protected categoriaFormService: CategoriaFormService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ categoria }) => {
      this.categoria = categoria;
      if (categoria) {
        this.updateForm(categoria);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('jhipsterApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const categoria = this.categoriaFormService.getCategoria(this.editForm);
    if (categoria.id !== null) {
      this.subscribeToSaveResponse(this.categoriaService.update(categoria));
    } else {
      this.subscribeToSaveResponse(this.categoriaService.create(categoria));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICategoria>>): void {
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

  protected updateForm(categoria: ICategoria): void {
    this.categoria = categoria;
    this.categoriaFormService.resetForm(this.editForm, categoria);
  }
}
