import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPelicula, NewPelicula } from '../pelicula.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPelicula for edit and NewPeliculaFormGroupInput for create.
 */
type PeliculaFormGroupInput = IPelicula | PartialWithRequiredKeyOf<NewPelicula>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPelicula | NewPelicula> = Omit<T, 'fechaEstreno'> & {
  fechaEstreno?: string | null;
};

type PeliculaFormRawValue = FormValueOf<IPelicula>;

type NewPeliculaFormRawValue = FormValueOf<NewPelicula>;

type PeliculaFormDefaults = Pick<NewPelicula, 'id' | 'fechaEstreno' | 'enCines' | 'actors'>;

type PeliculaFormGroupContent = {
  id: FormControl<PeliculaFormRawValue['id'] | NewPelicula['id']>;
  titulo: FormControl<PeliculaFormRawValue['titulo']>;
  fechaEstreno: FormControl<PeliculaFormRawValue['fechaEstreno']>;
  descripcion: FormControl<PeliculaFormRawValue['descripcion']>;
  enCines: FormControl<PeliculaFormRawValue['enCines']>;
  director: FormControl<PeliculaFormRawValue['director']>;
  actors: FormControl<PeliculaFormRawValue['actors']>;
};

export type PeliculaFormGroup = FormGroup<PeliculaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PeliculaFormService {
  createPeliculaFormGroup(pelicula: PeliculaFormGroupInput = { id: null }): PeliculaFormGroup {
    const peliculaRawValue = this.convertPeliculaToPeliculaRawValue({
      ...this.getFormDefaults(),
      ...pelicula,
    });
    return new FormGroup<PeliculaFormGroupContent>({
      id: new FormControl(
        { value: peliculaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      titulo: new FormControl(peliculaRawValue.titulo, {
        validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
      }),
      fechaEstreno: new FormControl(peliculaRawValue.fechaEstreno),
      descripcion: new FormControl(peliculaRawValue.descripcion, {
        validators: [Validators.minLength(20), Validators.maxLength(500)],
      }),
      enCines: new FormControl(peliculaRawValue.enCines),
      director: new FormControl(peliculaRawValue.director),
      actors: new FormControl(peliculaRawValue.actors ?? []),
    });
  }

  getPelicula(form: PeliculaFormGroup): IPelicula | NewPelicula {
    return this.convertPeliculaRawValueToPelicula(form.getRawValue() as PeliculaFormRawValue | NewPeliculaFormRawValue);
  }

  resetForm(form: PeliculaFormGroup, pelicula: PeliculaFormGroupInput): void {
    const peliculaRawValue = this.convertPeliculaToPeliculaRawValue({ ...this.getFormDefaults(), ...pelicula });
    form.reset(
      {
        ...peliculaRawValue,
        id: { value: peliculaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PeliculaFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaEstreno: currentTime,
      enCines: false,
      actors: [],
    };
  }

  private convertPeliculaRawValueToPelicula(rawPelicula: PeliculaFormRawValue | NewPeliculaFormRawValue): IPelicula | NewPelicula {
    return {
      ...rawPelicula,
      fechaEstreno: dayjs(rawPelicula.fechaEstreno, DATE_TIME_FORMAT),
    };
  }

  private convertPeliculaToPeliculaRawValue(
    pelicula: IPelicula | (Partial<NewPelicula> & PeliculaFormDefaults)
  ): PeliculaFormRawValue | PartialWithRequiredKeyOf<NewPeliculaFormRawValue> {
    return {
      ...pelicula,
      fechaEstreno: pelicula.fechaEstreno ? pelicula.fechaEstreno.format(DATE_TIME_FORMAT) : undefined,
      actors: pelicula.actors ?? [],
    };
  }
}
