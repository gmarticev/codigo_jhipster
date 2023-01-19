import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDirector } from '../director.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../director.test-samples';

import { DirectorService } from './director.service';

const requireRestSample: IDirector = {
  ...sampleWithRequiredData,
};

describe('Director Service', () => {
  let service: DirectorService;
  let httpMock: HttpTestingController;
  let expectedResult: IDirector | IDirector[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DirectorService);
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

    it('should create a Director', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const director = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(director).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Director', () => {
      const director = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(director).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Director', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Director', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Director', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDirectorToCollectionIfMissing', () => {
      it('should add a Director to an empty array', () => {
        const director: IDirector = sampleWithRequiredData;
        expectedResult = service.addDirectorToCollectionIfMissing([], director);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(director);
      });

      it('should not add a Director to an array that contains it', () => {
        const director: IDirector = sampleWithRequiredData;
        const directorCollection: IDirector[] = [
          {
            ...director,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDirectorToCollectionIfMissing(directorCollection, director);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Director to an array that doesn't contain it", () => {
        const director: IDirector = sampleWithRequiredData;
        const directorCollection: IDirector[] = [sampleWithPartialData];
        expectedResult = service.addDirectorToCollectionIfMissing(directorCollection, director);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(director);
      });

      it('should add only unique Director to an array', () => {
        const directorArray: IDirector[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const directorCollection: IDirector[] = [sampleWithRequiredData];
        expectedResult = service.addDirectorToCollectionIfMissing(directorCollection, ...directorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const director: IDirector = sampleWithRequiredData;
        const director2: IDirector = sampleWithPartialData;
        expectedResult = service.addDirectorToCollectionIfMissing([], director, director2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(director);
        expect(expectedResult).toContain(director2);
      });

      it('should accept null and undefined values', () => {
        const director: IDirector = sampleWithRequiredData;
        expectedResult = service.addDirectorToCollectionIfMissing([], null, director, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(director);
      });

      it('should return initial array if no Director is added', () => {
        const directorCollection: IDirector[] = [sampleWithRequiredData];
        expectedResult = service.addDirectorToCollectionIfMissing(directorCollection, undefined, null);
        expect(expectedResult).toEqual(directorCollection);
      });
    });

    describe('compareDirector', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDirector(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDirector(entity1, entity2);
        const compareResult2 = service.compareDirector(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDirector(entity1, entity2);
        const compareResult2 = service.compareDirector(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDirector(entity1, entity2);
        const compareResult2 = service.compareDirector(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
