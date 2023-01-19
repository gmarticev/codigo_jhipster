import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IActor, NewActor } from '../actor.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IActor for edit and NewActorFormGroupInput for create.
 */
type ActorFormGroupInput = IActor | PartialWithRequiredKeyOf<NewActor>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IActor | NewActor> = Omit<T, 'fechaNacimiento'> & {
  fechaNacimiento?: string | null;
};

type ActorFormRawValue = FormValueOf<IActor>;

type NewActorFormRawValue = FormValueOf<NewActor>;

type ActorFormDefaults = Pick<NewActor, 'id' | 'fechaNacimiento' | 'peliculas'>;

type ActorFormGroupContent = {
  id: FormControl<ActorFormRawValue['id'] | NewActor['id']>;
  nombre: FormControl<ActorFormRawValue['nombre']>;
  apellidos: FormControl<ActorFormRawValue['apellidos']>;
  fechaNacimiento: FormControl<ActorFormRawValue['fechaNacimiento']>;
  peliculas: FormControl<ActorFormRawValue['peliculas']>;
};

export type ActorFormGroup = FormGroup<ActorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ActorFormService {
  createActorFormGroup(actor: ActorFormGroupInput = { id: null }): ActorFormGroup {
    const actorRawValue = this.convertActorToActorRawValue({
      ...this.getFormDefaults(),
      ...actor,
    });
    return new FormGroup<ActorFormGroupContent>({
      id: new FormControl(
        { value: actorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombre: new FormControl(actorRawValue.nombre, {
        validators: [Validators.minLength(3), Validators.maxLength(40)],
      }),
      apellidos: new FormControl(actorRawValue.apellidos),
      fechaNacimiento: new FormControl(actorRawValue.fechaNacimiento),
      peliculas: new FormControl(actorRawValue.peliculas ?? []),
    });
  }

  getActor(form: ActorFormGroup): IActor | NewActor {
    return this.convertActorRawValueToActor(form.getRawValue() as ActorFormRawValue | NewActorFormRawValue);
  }

  resetForm(form: ActorFormGroup, actor: ActorFormGroupInput): void {
    const actorRawValue = this.convertActorToActorRawValue({ ...this.getFormDefaults(), ...actor });
    form.reset(
      {
        ...actorRawValue,
        id: { value: actorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ActorFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaNacimiento: currentTime,
      peliculas: [],
    };
  }

  private convertActorRawValueToActor(rawActor: ActorFormRawValue | NewActorFormRawValue): IActor | NewActor {
    return {
      ...rawActor,
      fechaNacimiento: dayjs(rawActor.fechaNacimiento, DATE_TIME_FORMAT),
    };
  }

  private convertActorToActorRawValue(
    actor: IActor | (Partial<NewActor> & ActorFormDefaults)
  ): ActorFormRawValue | PartialWithRequiredKeyOf<NewActorFormRawValue> {
    return {
      ...actor,
      fechaNacimiento: actor.fechaNacimiento ? actor.fechaNacimiento.format(DATE_TIME_FORMAT) : undefined,
      peliculas: actor.peliculas ?? [],
    };
  }
}
