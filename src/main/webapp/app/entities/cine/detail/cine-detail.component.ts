import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICine } from '../cine.model';

@Component({
  selector: 'jhi-cine-detail',
  templateUrl: './cine-detail.component.html',
})
export class CineDetailComponent implements OnInit {
  cine: ICine | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cine }) => {
      this.cine = cine;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
