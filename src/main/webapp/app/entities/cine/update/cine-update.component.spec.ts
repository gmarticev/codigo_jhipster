import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CineFormService } from './cine-form.service';
import { CineService } from '../service/cine.service';
import { ICine } from '../cine.model';

import { CineUpdateComponent } from './cine-update.component';

describe('Cine Management Update Component', () => {
  let comp: CineUpdateComponent;
  let fixture: ComponentFixture<CineUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cineFormService: CineFormService;
  let cineService: CineService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CineUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CineUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CineUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cineFormService = TestBed.inject(CineFormService);
    cineService = TestBed.inject(CineService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const cine: ICine = { id: 456 };

      activatedRoute.data = of({ cine });
      comp.ngOnInit();

      expect(comp.cine).toEqual(cine);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICine>>();
      const cine = { id: 123 };
      jest.spyOn(cineFormService, 'getCine').mockReturnValue(cine);
      jest.spyOn(cineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cine }));
      saveSubject.complete();

      // THEN
      expect(cineFormService.getCine).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cineService.update).toHaveBeenCalledWith(expect.objectContaining(cine));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICine>>();
      const cine = { id: 123 };
      jest.spyOn(cineFormService, 'getCine').mockReturnValue({ id: null });
      jest.spyOn(cineService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cine: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cine }));
      saveSubject.complete();

      // THEN
      expect(cineFormService.getCine).toHaveBeenCalled();
      expect(cineService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICine>>();
      const cine = { id: 123 };
      jest.spyOn(cineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cineService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
