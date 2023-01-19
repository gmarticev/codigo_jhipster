import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EstrenoFormService } from './estreno-form.service';
import { EstrenoService } from '../service/estreno.service';
import { IEstreno } from '../estreno.model';
import { IPelicula } from 'app/entities/pelicula/pelicula.model';
import { PeliculaService } from 'app/entities/pelicula/service/pelicula.service';

import { EstrenoUpdateComponent } from './estreno-update.component';

describe('Estreno Management Update Component', () => {
  let comp: EstrenoUpdateComponent;
  let fixture: ComponentFixture<EstrenoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let estrenoFormService: EstrenoFormService;
  let estrenoService: EstrenoService;
  let peliculaService: PeliculaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EstrenoUpdateComponent],
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
      .overrideTemplate(EstrenoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EstrenoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    estrenoFormService = TestBed.inject(EstrenoFormService);
    estrenoService = TestBed.inject(EstrenoService);
    peliculaService = TestBed.inject(PeliculaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call pelicula query and add missing value', () => {
      const estreno: IEstreno = { id: 456 };
      const pelicula: IPelicula = { id: 82004 };
      estreno.pelicula = pelicula;

      const peliculaCollection: IPelicula[] = [{ id: 4501 }];
      jest.spyOn(peliculaService, 'query').mockReturnValue(of(new HttpResponse({ body: peliculaCollection })));
      const expectedCollection: IPelicula[] = [pelicula, ...peliculaCollection];
      jest.spyOn(peliculaService, 'addPeliculaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ estreno });
      comp.ngOnInit();

      expect(peliculaService.query).toHaveBeenCalled();
      expect(peliculaService.addPeliculaToCollectionIfMissing).toHaveBeenCalledWith(peliculaCollection, pelicula);
      expect(comp.peliculasCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const estreno: IEstreno = { id: 456 };
      const pelicula: IPelicula = { id: 88916 };
      estreno.pelicula = pelicula;

      activatedRoute.data = of({ estreno });
      comp.ngOnInit();

      expect(comp.peliculasCollection).toContain(pelicula);
      expect(comp.estreno).toEqual(estreno);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstreno>>();
      const estreno = { id: 123 };
      jest.spyOn(estrenoFormService, 'getEstreno').mockReturnValue(estreno);
      jest.spyOn(estrenoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estreno });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: estreno }));
      saveSubject.complete();

      // THEN
      expect(estrenoFormService.getEstreno).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(estrenoService.update).toHaveBeenCalledWith(expect.objectContaining(estreno));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstreno>>();
      const estreno = { id: 123 };
      jest.spyOn(estrenoFormService, 'getEstreno').mockReturnValue({ id: null });
      jest.spyOn(estrenoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estreno: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: estreno }));
      saveSubject.complete();

      // THEN
      expect(estrenoFormService.getEstreno).toHaveBeenCalled();
      expect(estrenoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstreno>>();
      const estreno = { id: 123 };
      jest.spyOn(estrenoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estreno });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(estrenoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePelicula', () => {
      it('Should forward to peliculaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(peliculaService, 'comparePelicula');
        comp.comparePelicula(entity, entity2);
        expect(peliculaService.comparePelicula).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
