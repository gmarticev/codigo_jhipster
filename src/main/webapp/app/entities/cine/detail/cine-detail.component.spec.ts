import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CineDetailComponent } from './cine-detail.component';

describe('Cine Management Detail Component', () => {
  let comp: CineDetailComponent;
  let fixture: ComponentFixture<CineDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CineDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ cine: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CineDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CineDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load cine on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.cine).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
