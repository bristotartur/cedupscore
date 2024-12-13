import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParticipantsFilesManagerComponent } from './participants-files-manager.component';

describe('ParticipantsFilesManagerComponent', () => {
  let component: ParticipantsFilesManagerComponent;
  let fixture: ComponentFixture<ParticipantsFilesManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParticipantsFilesManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParticipantsFilesManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
