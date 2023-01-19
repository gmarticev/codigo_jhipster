import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../director.test-samples';

import { DirectorFormService } from './director-form.service';

describe('Director Form Service', () => {
  let service: DirectorFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DirectorFormService);
  });

  describe('Service methods', () => {
    describe('createDirectorFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDirectorFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            apellidos: expect.any(Object),
          })
        );
      });

      it('passing IDirector should create a new form with FormGroup', () => {
        const formGroup = service.createDirectorFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            apellidos: expect.any(Object),
          })
        );
      });
    });

    describe('getDirector', () => {
      it('should return NewDirector for default Director initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDirectorFormGroup(sampleWithNewData);

        const director = service.getDirector(formGroup) as any;

        expect(director).toMatchObject(sampleWithNewData);
      });

      it('should return NewDirector for empty Director initial value', () => {
        const formGroup = service.createDirectorFormGroup();

        const director = service.getDirector(formGroup) as any;

        expect(director).toMatchObject({});
      });

      it('should return IDirector', () => {
        const formGroup = service.createDirectorFormGroup(sampleWithRequiredData);

        const director = service.getDirector(formGroup) as any;

        expect(director).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDirector should not enable id FormControl', () => {
        const formGroup = service.createDirectorFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDirector should disable id FormControl', () => {
        const formGroup = service.createDirectorFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
