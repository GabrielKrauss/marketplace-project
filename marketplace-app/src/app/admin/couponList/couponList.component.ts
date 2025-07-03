import { Component, OnChanges, OnInit } from '@angular/core';
import { ProductService } from '../../service/product.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../service/category.service';
import { Category, Coupon, Customer, CustomerType, Product } from '../../models';
import { CustomerService } from '../../service/customer.service';
import { RouterModule } from '@angular/router';
import { CouponService } from '../../service/coupon.service';

@Component({
  selector: 'app-couponList',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
  styleUrl: './couponList.component.css',
  templateUrl: './couponList.component.html',
})
export class CouponListComponent implements OnInit, OnChanges {
  coupons: Coupon[] = [];
  dropdownOpen = false;
  searchTerm: string = '';
  selectedCoupon: Coupon = {} as Coupon;
  newCoupon: Coupon = {
    id: 0,
    code: '',
    discountValue: 0.0,
    discountPercentage: 0.0,
    isActive: true
  };
  filteredCouponList: Coupon[] = [];
  buttonFunctionChanges: boolean = false;
  constructor(private couponService: CouponService) {}

  ngOnInit(): void {
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }
  ngOnChanges(): void {
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  findAll(): void {
    this.couponService.findAll().subscribe((data) => {
      this.coupons = data as unknown as Coupon[];
      this.buttonFunctionChanges = true;
      this.filteredCouponList = [...this.coupons];
    });
  }

  findAllActive(): void {
    this.couponService.findAllActiveCoupons().subscribe((data) => {
      this.coupons = data as unknown as Coupon[];
      this.buttonFunctionChanges = false;
      this.filteredCouponList = [...this.coupons];
    });
  }

  edit(coupon: Coupon): void {
    this.selectedCoupon = { ...coupon };
  }

  updateCoupon(): void {
    if (!this.selectedCoupon) return;

    this.couponService
      .update(this.selectedCoupon.id, this.selectedCoupon)
      .subscribe({
        next: (updatedCoupon) => {
          const index = this.coupons.findIndex(
            (p) => p.id === updatedCoupon.id
          );
          if (index !== -1) {
            this.coupons[index] = updatedCoupon;
          }
          this.closeModal();
        },
        error: (err) => console.error('Erro ao atualizar coupon:', err),
      });
  }

  delete(productId: number): void {
    this.couponService.delete(productId).subscribe(() => {
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
    });
  }

  insert(): void {
    this.couponService.insert(this.newCoupon).subscribe(() => {
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
      this.resetNewCoupon();
      this.closeModal();
    });
  }


  resetNewCoupon(): void {
    this.newCoupon = {
      id: 0,
      code: '',
      discountValue: 0.0,
      discountPercentage: 0.0,
      isActive: true
    };
  }

  openEditModal(coupon: Coupon): void {
    this.selectedCoupon = { ...coupon };
    this.dropdownOpen = false;
    this.searchTerm = ''; // Limpa o termo de pesquisa
    this.dropdownOpen = false;
  }

  openAddModal(): void {
    this.dropdownOpen = false;
    this.searchTerm = ''; // Limpa o termo de pesquisa
    this.dropdownOpen = false;
  }

  closeModal(): void {
    this.dropdownOpen = false;
    this.searchTerm = ''; // Limpa o termo de pesquisa
    this.dropdownOpen = false;
    this.resetNewCoupon();
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  filterCoupons(): void {
    if (!this.searchTerm) {
      this.filteredCouponList = this.coupons;
    } else {
      this.filteredCouponList = this.coupons.filter((coupon) =>
        coupon.code.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }
}
