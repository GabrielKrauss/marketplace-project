import { TestBed } from '@angular/core/testing';
import { DeliveryAddressComponent } from './deliveryAddress.component';

describe('DeliveryAddressComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeliveryAddressComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(DeliveryAddressComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
