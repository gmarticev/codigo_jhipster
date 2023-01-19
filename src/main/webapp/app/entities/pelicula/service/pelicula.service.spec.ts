import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPelicula } from '../pelicula.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../pelicula.test-samples';

import { PeliculaService, RestPelicula } from './pelicula.service';

const requireRestSample: RestPelicula = {
  ...sampleWithRequiredData,
  fechaEstreno: sampleWithRequiredData.fechaEstreno?.toJSON(),
};

describe('Pelicula Service', () => {
  let service: PeliculaService;
  let httpMock: HttpTestingController;
  let expectedResult: IPelicula | IPelicula[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PeliculaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Pelicula', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const pelicula = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(pelicula).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Pelicula', () => {
      const pelicula = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(pelicula).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Pelicula', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Pelicula', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Pelicula', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPeliculaToCollectionIfMissing', () => {
      it('should add a Pelicula to an empty array', () => {
        const pelicula: IPelicula = sampleWithRequiredData;
        expectedResult = service.addPeliculaToCollectionIfMissing([], pelicula);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pelicula);
      });

      it('should not add a Pelicula to an array that contains it', () => {
        const pelicula: IPelicula = sampleWithRequiredData;
        const peliculaCollection: IPelicula[] = [
          {
            ...pelicula,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPeliculaToCollectionIfMissing(peliculaCollection, pelicula);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Pelicula to an array that doesn't contain it", () => {
        const pelicula: IPelicula = sampleWithRequiredData;
        const peliculaCollection: IPelicula[] = [sampleWithPartialData];
        expectedResult = service.addPeliculaToCollectionIfMissing(peliculaCollection, pelicula);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pelicula);
      });

      it('should add only unique Pelicula to an array', () => {
        const peliculaArray: IPelicula[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const peliculaCollection: IPelicula[] = [sampleWithRequiredData];
        expectedResult = service.addPeliculaToCollectionIfMissing(peliculaCollection, ...peliculaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pelicula: IPelicula = sampleWithRequiredData;
        const pelicula2: IPelicula = sampleWithPartialData;
        expectedResult = service.addPeliculaToCollectionIfMissing([], pelicula, pelicula2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pelicula);
        expect(expectedResult).toContain(pelicula2);
      });

      it('should accept null and undefined values', () => {
        const pelicula: IPelicula = sampleWithRequiredData;
        expectedResult = service.addPeliculaToCollectionIfMissing([], null, pelicula, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pelicula);
      });

      it('should return initial array if no Pelicula is added', () => {
        const peliculaCollection: IPelicula[] = [sampleWithRequiredData];
        expectedResult = service.addPeliculaToCollectionIfMissing(peliculaCollection, undefined, null);
        expect(expectedResult).toEqual(peliculaCollection);
      });
    });

    describe('comparePelicula', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePelicula(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePelicula(entity1, entity2);
        const compareResult2 = service.comparePelicula(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePelicula(entity1, entity2);
        const compareResult2 = service.comparePelicula(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePelicula(entity1, entity2);
        const compareResult2 = service.comparePelicula(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
