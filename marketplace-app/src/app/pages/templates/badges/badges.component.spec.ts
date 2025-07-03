import { TestBed } from '@angular/core/testing';
import { BadgesComponent } from './badges.component';

describe('BadgesComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BadgesComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(BadgesComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
