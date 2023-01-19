import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICine, NewCine } from '../cine.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICine for edit and NewCineFormGroupInput for create.
 */
type CineFormGroupInput = ICine | PartialWithRequiredKeyOf<NewCine>;

type CineFormDefaults = Pick<NewCine, 'id'>;

type CineFormGroupContent = {
  id: FormControl<ICine['id'] | NewCine['id']>;
  nombre: FormControl<ICine['nombre']>;
  direccion: FormControl<ICine['direccion']>;
};

export type CineFormGroup = FormGroup<CineFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CineFormService {
  createCineFormGroup(cine: CineFormGroupInput = { id: null }): CineFormGroup {
    const cineRawValue = {
      ...this.getFormDefaults(),
      ...cine,
    };
    return new FormGroup<CineFormGroupContent>({
      id: new FormControl(
        { value: cineRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombre: new FormControl(cineRawValue.nombre, {
        validators: [Validators.minLength(5), Validators.maxLength(50)],
      }),
      direccion: new FormControl(cineRawValue.direccion, {
        validators: [Validators.minLength(20), Validators.maxLength(500)],
      }),
    });
  }

  getCine(form: CineFormGroup): ICine | NewCine {
    return form.getRawValue() as ICine | NewCine;
  }

  resetForm(form: CineFormGroup, cine: CineFormGroupInput): void {
    const cineRawValue = { ...this.getFormDefaults(), ...cine };
    form.reset(
      {
        ...cineRawValue,
        id: { value: cineRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CineFormDefaults {
    return {
      id: null,
    };
  }
}
