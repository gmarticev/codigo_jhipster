import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../pelicula.test-samples';

import { PeliculaFormService } from './pelicula-form.service';

describe('Pelicula Form Service', () => {
  let service: PeliculaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PeliculaFormService);
  });

  describe('Service methods', () => {
    describe('createPeliculaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPeliculaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            fechaEstreno: expect.any(Object),
            descripcion: expect.any(Object),
            enCines: expect.any(Object),
            director: expect.any(Object),
            actors: expect.any(Object),
          })
        );
      });

      it('passing IPelicula should create a new form with FormGroup', () => {
        const formGroup = service.createPeliculaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titulo: expect.any(Object),
            fechaEstreno: expect.any(Object),
            descripcion: expect.any(Object),
            enCines: expect.any(Object),
            director: expect.any(Object),
            actors: expect.any(Object),
          })
        );
      });
    });

    describe('getPelicula', () => {
      it('should return NewPelicula for default Pelicula initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPeliculaFormGroup(sampleWithNewData);

        const pelicula = service.getPelicula(formGroup) as any;

        expect(pelicula).toMatchObject(sampleWithNewData);
      });

      it('should return NewPelicula for empty Pelicula initial value', () => {
        const formGroup = service.createPeliculaFormGroup();

        const pelicula = service.getPelicula(formGroup) as any;

        expect(pelicula).toMatchObject({});
      });

      it('should return IPelicula', () => {
        const formGroup = service.createPeliculaFormGroup(sampleWithRequiredData);

        const pelicula = service.getPelicula(formGroup) as any;

        expect(pelicula).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPelicula should not enable id FormControl', () => {
        const formGroup = service.createPeliculaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPelicula should disable id FormControl', () => {
        const formGroup = service.createPeliculaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
