import { TestBed } from '@angular/core/testing';
import { AccessDeniedComponent } from './accessDenied.component';

describe('AccessDeniedComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccessDeniedComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AccessDeniedComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
