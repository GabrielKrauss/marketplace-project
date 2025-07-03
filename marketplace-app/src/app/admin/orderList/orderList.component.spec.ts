import { TestBed } from '@angular/core/testing';
import { OrderListComponent } from './orderList.component';

describe('OrderListComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderListComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(OrderListComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
