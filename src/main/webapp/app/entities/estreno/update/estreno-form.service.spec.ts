import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../estreno.test-samples';

import { EstrenoFormService } from './estreno-form.service';

describe('Estreno Form Service', () => {
  let service: EstrenoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EstrenoFormService);
  });

  describe('Service methods', () => {
    describe('createEstrenoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEstrenoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fecha: expect.any(Object),
            lugar: expect.any(Object),
            pelicula: expect.any(Object),
          })
        );
      });

      it('passing IEstreno should create a new form with FormGroup', () => {
        const formGroup = service.createEstrenoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fecha: expect.any(Object),
            lugar: expect.any(Object),
            pelicula: expect.any(Object),
          })
        );
      });
    });

    describe('getEstreno', () => {
      it('should return NewEstreno for default Estreno initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEstrenoFormGroup(sampleWithNewData);

        const estreno = service.getEstreno(formGroup) as any;

        expect(estreno).toMatchObject(sampleWithNewData);
      });

      it('should return NewEstreno for empty Estreno initial value', () => {
        const formGroup = service.createEstrenoFormGroup();

        const estreno = service.getEstreno(formGroup) as any;

        expect(estreno).toMatchObject({});
      });

      it('should return IEstreno', () => {
        const formGroup = service.createEstrenoFormGroup(sampleWithRequiredData);

        const estreno = service.getEstreno(formGroup) as any;

        expect(estreno).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEstreno should not enable id FormControl', () => {
        const formGroup = service.createEstrenoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEstreno should disable id FormControl', () => {
        const formGroup = service.createEstrenoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
