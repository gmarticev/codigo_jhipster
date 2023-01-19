import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICine } from '../cine.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../cine.test-samples';

import { CineService } from './cine.service';

const requireRestSample: ICine = {
  ...sampleWithRequiredData,
};

describe('Cine Service', () => {
  let service: CineService;
  let httpMock: HttpTestingController;
  let expectedResult: ICine | ICine[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CineService);
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

    it('should create a Cine', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const cine = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cine).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Cine', () => {
      const cine = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cine).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Cine', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Cine', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Cine', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCineToCollectionIfMissing', () => {
      it('should add a Cine to an empty array', () => {
        const cine: ICine = sampleWithRequiredData;
        expectedResult = service.addCineToCollectionIfMissing([], cine);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cine);
      });

      it('should not add a Cine to an array that contains it', () => {
        const cine: ICine = sampleWithRequiredData;
        const cineCollection: ICine[] = [
          {
            ...cine,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCineToCollectionIfMissing(cineCollection, cine);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Cine to an array that doesn't contain it", () => {
        const cine: ICine = sampleWithRequiredData;
        const cineCollection: ICine[] = [sampleWithPartialData];
        expectedResult = service.addCineToCollectionIfMissing(cineCollection, cine);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cine);
      });

      it('should add only unique Cine to an array', () => {
        const cineArray: ICine[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cineCollection: ICine[] = [sampleWithRequiredData];
        expectedResult = service.addCineToCollectionIfMissing(cineCollection, ...cineArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cine: ICine = sampleWithRequiredData;
        const cine2: ICine = sampleWithPartialData;
        expectedResult = service.addCineToCollectionIfMissing([], cine, cine2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cine);
        expect(expectedResult).toContain(cine2);
      });

      it('should accept null and undefined values', () => {
        const cine: ICine = sampleWithRequiredData;
        expectedResult = service.addCineToCollectionIfMissing([], null, cine, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cine);
      });

      it('should return initial array if no Cine is added', () => {
        const cineCollection: ICine[] = [sampleWithRequiredData];
        expectedResult = service.addCineToCollectionIfMissing(cineCollection, undefined, null);
        expect(expectedResult).toEqual(cineCollection);
      });
    });

    describe('compareCine', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCine(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCine(entity1, entity2);
        const compareResult2 = service.compareCine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCine(entity1, entity2);
        const compareResult2 = service.compareCine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCine(entity1, entity2);
        const compareResult2 = service.compareCine(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
