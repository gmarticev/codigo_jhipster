import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../cine.test-samples';

import { CineFormService } from './cine-form.service';

describe('Cine Form Service', () => {
  let service: CineFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CineFormService);
  });

  describe('Service methods', () => {
    describe('createCineFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCineFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            direccion: expect.any(Object),
          })
        );
      });

      it('passing ICine should create a new form with FormGroup', () => {
        const formGroup = service.createCineFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            direccion: expect.any(Object),
          })
        );
      });
    });

    describe('getCine', () => {
      it('should return NewCine for default Cine initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCineFormGroup(sampleWithNewData);

        const cine = service.getCine(formGroup) as any;

        expect(cine).toMatchObject(sampleWithNewData);
      });

      it('should return NewCine for empty Cine initial value', () => {
        const formGroup = service.createCineFormGroup();

        const cine = service.getCine(formGroup) as any;

        expect(cine).toMatchObject({});
      });

      it('should return ICine', () => {
        const formGroup = service.createCineFormGroup(sampleWithRequiredData);

        const cine = service.getCine(formGroup) as any;

        expect(cine).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICine should not enable id FormControl', () => {
        const formGroup = service.createCineFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCine should disable id FormControl', () => {
        const formGroup = service.createCineFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
