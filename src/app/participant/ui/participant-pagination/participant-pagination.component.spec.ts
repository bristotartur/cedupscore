import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParticipantPaginationComponent } from './participant-pagination.component';

describe('ParticipantPageComponent', () => {
  let component: ParticipantPaginationComponent;
  let fixture: ComponentFixture<ParticipantPaginationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParticipantPaginationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParticipantPaginationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
