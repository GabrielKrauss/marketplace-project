import { TestBed } from '@angular/core/testing';
import { CustomerListComponent } from './customerList.component';

describe('CustomerListComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerListComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(CustomerListComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
