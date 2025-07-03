import { TestBed } from '@angular/core/testing';
import { ProductListComponent } from './productList.component';

describe('ProductListComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductListComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(ProductListComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
