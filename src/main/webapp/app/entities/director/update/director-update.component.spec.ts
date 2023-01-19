import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DirectorFormService } from './director-form.service';
import { DirectorService } from '../service/director.service';
import { IDirector } from '../director.model';

import { DirectorUpdateComponent } from './director-update.component';

describe('Director Management Update Component', () => {
  let comp: DirectorUpdateComponent;
  let fixture: ComponentFixture<DirectorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let directorFormService: DirectorFormService;
  let directorService: DirectorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DirectorUpdateComponent],
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
      .overrideTemplate(DirectorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DirectorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    directorFormService = TestBed.inject(DirectorFormService);
    directorService = TestBed.inject(DirectorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const director: IDirector = { id: 456 };

      activatedRoute.data = of({ director });
      comp.ngOnInit();

      expect(comp.director).toEqual(director);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDirector>>();
      const director = { id: 123 };
      jest.spyOn(directorFormService, 'getDirector').mockReturnValue(director);
      jest.spyOn(directorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ director });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: director }));
      saveSubject.complete();

      // THEN
      expect(directorFormService.getDirector).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(directorService.update).toHaveBeenCalledWith(expect.objectContaining(director));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDirector>>();
      const director = { id: 123 };
      jest.spyOn(directorFormService, 'getDirector').mockReturnValue({ id: null });
      jest.spyOn(directorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ director: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: director }));
      saveSubject.complete();

      // THEN
      expect(directorFormService.getDirector).toHaveBeenCalled();
      expect(directorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDirector>>();
      const director = { id: 123 };
      jest.spyOn(directorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ director });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(directorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
