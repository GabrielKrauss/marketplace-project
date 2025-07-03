import { TestBed } from '@angular/core/testing';
import { CategoryListComponent } from './categoryList.component';

describe('CategoryListComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryListComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(CategoryListComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
