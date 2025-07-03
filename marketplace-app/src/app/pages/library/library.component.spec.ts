import { TestBed } from '@angular/core/testing';
import { LibraryComponent } from './library.component';

describe('LibraryComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibraryComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(LibraryComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
