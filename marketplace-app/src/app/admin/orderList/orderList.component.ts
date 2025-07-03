import { Component, OnChanges, OnInit } from '@angular/core';
import { ProductService } from '../../service/product.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../service/category.service';
import { Address, Coupon, Customer, Order, OrderRequest, OrderStatus, Product } from '../../models';
import { OrderService } from '../../service/order.service';
import { CustomerService } from '../../service/customer.service';
import { CouponService } from '../../service/coupon.service';
import { AddressService } from '../../service/address.service';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-orderList',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
  styleUrl: './orderList.component.css',
  templateUrl: './orderList.component.html',
})
export class OrderListComponent implements OnInit, OnChanges {
  orderStatusList: string[] = Object.values(OrderStatus);
  orders: Order[] = [];
  ordersActive: Order[] = [];
  customers: Customer[] = [];
  coupons: Coupon[] = [];
  dropdownOpen = false;
  searchTerm: string = '';
  selectedOrder: Order = {} as Order;
  originalOrder: OrderRequest = {} as OrderRequest;
  modalOpen = false;
  selectedAddress: Address = {} as Address;

  buttonFunctionChanges: boolean = false;

  newOrder: OrderRequest = {
    id: 0,
    orderStatus: OrderStatus.WAITING_PAYMENT,
    customerId: 0,
    addressId: 0,
    items: [],
    coupon: undefined
  };

  constructor(
    private orderService: OrderService,
    private customerService: CustomerService,
    private couponService: CouponService,
    private addressSerive: AddressService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.loadCoupons();
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }
  ngOnChanges(): void {
    this.loadCustomers();
    this.loadCoupons();
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  findAll(): void {
    this.orderService.findAll().subscribe((data) => {
      this.orders = data as unknown as Order[];
      this.buttonFunctionChanges = true;
    });
    
  }

  findAllActive(): void {
    this.orderService.findAllActive().subscribe((data) => {
      this.orders = data as unknown as Order[];
      this.buttonFunctionChanges = false;
    });
  }

  insert(): void {
    this.orderService.insert(this.newOrder).subscribe(() => {
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
      this.resetNewOrder();
      this.closeModal();
    });
  }

  edit(order: Order): void {
    this.selectedOrder = { ...order };    
  }

  updateOrder(): void {
    if (!this.selectedOrder) return;
    
    this.orderService
      .update(this.selectedOrder.id, this.selectedOrder)
      .subscribe({
        next: (updatedOrder) => {
          const index = this.orders.findIndex(
            (p) => p.id === updatedOrder.id
          );
          if (index !== -1) {
            this.orders[index] = updatedOrder;
          }
          this.closeModal();
        },
        error: (err) => console.error('Erro ao atualizar produto:', err),
        
      });    
  }
  delete(productId: number): void {
    this.orderService.delete(productId).subscribe(() => {
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
    });
  }

  resetNewOrder(): void {
    this.newOrder = {
      id: 0,
      orderStatus: OrderStatus.WAITING_PAYMENT,
      customerId: 0,
      addressId: 0,
      items: [],
      coupon: undefined
    };
  }
  openAddressModal(order: Order): void {
    this.addressSerive.findById(order.deliveryAddress.id).subscribe((data) => {
      this.selectedAddress = data as Address;
    });
  }

  openEditModal(order: Order): void {
    this.selectedOrder = { ...order };
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
    this.resetNewOrder();
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  loadCustomers(): void{
    this.customerService.findAllCustomers().subscribe((data) => {
          this.customers = data as unknown as Customer[];
        });
  }

  getCustomerById(customerId: number): Customer | undefined {
    return this.customers.find(customer => customer.id === customerId);
  }

  loadCoupons(): void{
    this.couponService.findAll().subscribe((data) => {
      this.coupons = data as unknown as Coupon[];
    });
  }
  
  getCouponById(couponId?: number): Coupon | undefined {
    return this.coupons.find(coupon => coupon.id === couponId);
  }

}
