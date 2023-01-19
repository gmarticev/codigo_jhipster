import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEstreno } from '../estreno.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../estreno.test-samples';

import { EstrenoService, RestEstreno } from './estreno.service';

const requireRestSample: RestEstreno = {
  ...sampleWithRequiredData,
  fecha: sampleWithRequiredData.fecha?.toJSON(),
};

describe('Estreno Service', () => {
  let service: EstrenoService;
  let httpMock: HttpTestingController;
  let expectedResult: IEstreno | IEstreno[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EstrenoService);
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

    it('should create a Estreno', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const estreno = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(estreno).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Estreno', () => {
      const estreno = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(estreno).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Estreno', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Estreno', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Estreno', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEstrenoToCollectionIfMissing', () => {
      it('should add a Estreno to an empty array', () => {
        const estreno: IEstreno = sampleWithRequiredData;
        expectedResult = service.addEstrenoToCollectionIfMissing([], estreno);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estreno);
      });

      it('should not add a Estreno to an array that contains it', () => {
        const estreno: IEstreno = sampleWithRequiredData;
        const estrenoCollection: IEstreno[] = [
          {
            ...estreno,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEstrenoToCollectionIfMissing(estrenoCollection, estreno);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Estreno to an array that doesn't contain it", () => {
        const estreno: IEstreno = sampleWithRequiredData;
        const estrenoCollection: IEstreno[] = [sampleWithPartialData];
        expectedResult = service.addEstrenoToCollectionIfMissing(estrenoCollection, estreno);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estreno);
      });

      it('should add only unique Estreno to an array', () => {
        const estrenoArray: IEstreno[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const estrenoCollection: IEstreno[] = [sampleWithRequiredData];
        expectedResult = service.addEstrenoToCollectionIfMissing(estrenoCollection, ...estrenoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const estreno: IEstreno = sampleWithRequiredData;
        const estreno2: IEstreno = sampleWithPartialData;
        expectedResult = service.addEstrenoToCollectionIfMissing([], estreno, estreno2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estreno);
        expect(expectedResult).toContain(estreno2);
      });

      it('should accept null and undefined values', () => {
        const estreno: IEstreno = sampleWithRequiredData;
        expectedResult = service.addEstrenoToCollectionIfMissing([], null, estreno, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estreno);
      });

      it('should return initial array if no Estreno is added', () => {
        const estrenoCollection: IEstreno[] = [sampleWithRequiredData];
        expectedResult = service.addEstrenoToCollectionIfMissing(estrenoCollection, undefined, null);
        expect(expectedResult).toEqual(estrenoCollection);
      });
    });

    describe('compareEstreno', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEstreno(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEstreno(entity1, entity2);
        const compareResult2 = service.compareEstreno(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEstreno(entity1, entity2);
        const compareResult2 = service.compareEstreno(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEstreno(entity1, entity2);
        const compareResult2 = service.compareEstreno(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
