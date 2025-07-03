import { TestBed } from '@angular/core/testing';
import { AlbumComponent } from './album.component';

describe('AlbumComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AlbumComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
