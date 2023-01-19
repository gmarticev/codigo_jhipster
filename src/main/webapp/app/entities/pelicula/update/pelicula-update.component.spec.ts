import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PeliculaFormService } from './pelicula-form.service';
import { PeliculaService } from '../service/pelicula.service';
import { IPelicula } from '../pelicula.model';
import { IDirector } from 'app/entities/director/director.model';
import { DirectorService } from 'app/entities/director/service/director.service';
import { IActor } from 'app/entities/actor/actor.model';
import { ActorService } from 'app/entities/actor/service/actor.service';

import { PeliculaUpdateComponent } from './pelicula-update.component';

describe('Pelicula Management Update Component', () => {
  let comp: PeliculaUpdateComponent;
  let fixture: ComponentFixture<PeliculaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let peliculaFormService: PeliculaFormService;
  let peliculaService: PeliculaService;
  let directorService: DirectorService;
  let actorService: ActorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PeliculaUpdateComponent],
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
      .overrideTemplate(PeliculaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PeliculaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    peliculaFormService = TestBed.inject(PeliculaFormService);
    peliculaService = TestBed.inject(PeliculaService);
    directorService = TestBed.inject(DirectorService);
    actorService = TestBed.inject(ActorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Director query and add missing value', () => {
      const pelicula: IPelicula = { id: 456 };
      const director: IDirector = { id: 18654 };
      pelicula.director = director;

      const directorCollection: IDirector[] = [{ id: 23186 }];
      jest.spyOn(directorService, 'query').mockReturnValue(of(new HttpResponse({ body: directorCollection })));
      const additionalDirectors = [director];
      const expectedCollection: IDirector[] = [...additionalDirectors, ...directorCollection];
      jest.spyOn(directorService, 'addDirectorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pelicula });
      comp.ngOnInit();

      expect(directorService.query).toHaveBeenCalled();
      expect(directorService.addDirectorToCollectionIfMissing).toHaveBeenCalledWith(
        directorCollection,
        ...additionalDirectors.map(expect.objectContaining)
      );
      expect(comp.directorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Actor query and add missing value', () => {
      const pelicula: IPelicula = { id: 456 };
      const actors: IActor[] = [{ id: 12892 }];
      pelicula.actors = actors;

      const actorCollection: IActor[] = [{ id: 4669 }];
      jest.spyOn(actorService, 'query').mockReturnValue(of(new HttpResponse({ body: actorCollection })));
      const additionalActors = [...actors];
      const expectedCollection: IActor[] = [...additionalActors, ...actorCollection];
      jest.spyOn(actorService, 'addActorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pelicula });
      comp.ngOnInit();

      expect(actorService.query).toHaveBeenCalled();
      expect(actorService.addActorToCollectionIfMissing).toHaveBeenCalledWith(
        actorCollection,
        ...additionalActors.map(expect.objectContaining)
      );
      expect(comp.actorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pelicula: IPelicula = { id: 456 };
      const director: IDirector = { id: 98940 };
      pelicula.director = director;
      const actor: IActor = { id: 82708 };
      pelicula.actors = [actor];

      activatedRoute.data = of({ pelicula });
      comp.ngOnInit();

      expect(comp.directorsSharedCollection).toContain(director);
      expect(comp.actorsSharedCollection).toContain(actor);
      expect(comp.pelicula).toEqual(pelicula);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPelicula>>();
      const pelicula = { id: 123 };
      jest.spyOn(peliculaFormService, 'getPelicula').mockReturnValue(pelicula);
      jest.spyOn(peliculaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pelicula });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pelicula }));
      saveSubject.complete();

      // THEN
      expect(peliculaFormService.getPelicula).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(peliculaService.update).toHaveBeenCalledWith(expect.objectContaining(pelicula));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPelicula>>();
      const pelicula = { id: 123 };
      jest.spyOn(peliculaFormService, 'getPelicula').mockReturnValue({ id: null });
      jest.spyOn(peliculaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pelicula: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pelicula }));
      saveSubject.complete();

      // THEN
      expect(peliculaFormService.getPelicula).toHaveBeenCalled();
      expect(peliculaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPelicula>>();
      const pelicula = { id: 123 };
      jest.spyOn(peliculaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pelicula });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(peliculaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDirector', () => {
      it('Should forward to directorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(directorService, 'compareDirector');
        comp.compareDirector(entity, entity2);
        expect(directorService.compareDirector).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareActor', () => {
      it('Should forward to actorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(actorService, 'compareActor');
        comp.compareActor(entity, entity2);
        expect(actorService.compareActor).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
