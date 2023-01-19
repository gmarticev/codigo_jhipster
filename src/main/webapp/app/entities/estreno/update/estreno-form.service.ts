import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEstreno, NewEstreno } from '../estreno.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEstreno for edit and NewEstrenoFormGroupInput for create.
 */
type EstrenoFormGroupInput = IEstreno | PartialWithRequiredKeyOf<NewEstreno>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEstreno | NewEstreno> = Omit<T, 'fecha'> & {
  fecha?: string | null;
};

type EstrenoFormRawValue = FormValueOf<IEstreno>;

type NewEstrenoFormRawValue = FormValueOf<NewEstreno>;

type EstrenoFormDefaults = Pick<NewEstreno, 'id' | 'fecha'>;

type EstrenoFormGroupContent = {
  id: FormControl<EstrenoFormRawValue['id'] | NewEstreno['id']>;
  fecha: FormControl<EstrenoFormRawValue['fecha']>;
  lugar: FormControl<EstrenoFormRawValue['lugar']>;
  pelicula: FormControl<EstrenoFormRawValue['pelicula']>;
};

export type EstrenoFormGroup = FormGroup<EstrenoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EstrenoFormService {
  createEstrenoFormGroup(estreno: EstrenoFormGroupInput = { id: null }): EstrenoFormGroup {
    const estrenoRawValue = this.convertEstrenoToEstrenoRawValue({
      ...this.getFormDefaults(),
      ...estreno,
    });
    return new FormGroup<EstrenoFormGroupContent>({
      id: new FormControl(
        { value: estrenoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fecha: new FormControl(estrenoRawValue.fecha),
      lugar: new FormControl(estrenoRawValue.lugar, {
        validators: [Validators.minLength(4), Validators.maxLength(150)],
      }),
      pelicula: new FormControl(estrenoRawValue.pelicula),
    });
  }

  getEstreno(form: EstrenoFormGroup): IEstreno | NewEstreno {
    return this.convertEstrenoRawValueToEstreno(form.getRawValue() as EstrenoFormRawValue | NewEstrenoFormRawValue);
  }

  resetForm(form: EstrenoFormGroup, estreno: EstrenoFormGroupInput): void {
    const estrenoRawValue = this.convertEstrenoToEstrenoRawValue({ ...this.getFormDefaults(), ...estreno });
    form.reset(
      {
        ...estrenoRawValue,
        id: { value: estrenoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EstrenoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fecha: currentTime,
    };
  }

  private convertEstrenoRawValueToEstreno(rawEstreno: EstrenoFormRawValue | NewEstrenoFormRawValue): IEstreno | NewEstreno {
    return {
      ...rawEstreno,
      fecha: dayjs(rawEstreno.fecha, DATE_TIME_FORMAT),
    };
  }

  private convertEstrenoToEstrenoRawValue(
    estreno: IEstreno | (Partial<NewEstreno> & EstrenoFormDefaults)
  ): EstrenoFormRawValue | PartialWithRequiredKeyOf<NewEstrenoFormRawValue> {
    return {
      ...estreno,
      fecha: estreno.fecha ? estreno.fecha.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
