import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParticipantEventRegistrationComponent } from './participant-event-registration.component';

describe('ParticipantEventRegistrationComponent', () => {
  let component: ParticipantEventRegistrationComponent;
  let fixture: ComponentFixture<ParticipantEventRegistrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParticipantEventRegistrationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParticipantEventRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
