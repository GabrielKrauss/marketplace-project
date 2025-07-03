import { TestBed } from '@angular/core/testing';
import { CouponListComponent } from './couponList.component';

describe('CouponListComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CouponListComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(CouponListComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
