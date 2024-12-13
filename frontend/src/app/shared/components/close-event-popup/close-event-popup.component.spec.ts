import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CloseEventPopupComponent } from './close-event-popup.component';

describe('CloseEventPopupComponent', () => {
  let component: CloseEventPopupComponent;
  let fixture: ComponentFixture<CloseEventPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CloseEventPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CloseEventPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
