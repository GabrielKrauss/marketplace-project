import { TestBed } from '@angular/core/testing';
import { PaymentComponent } from './payment.component';

describe('PaymentComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(PaymentComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
