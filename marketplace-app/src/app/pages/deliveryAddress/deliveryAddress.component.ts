import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../service/cart.service';
import { CustomerService } from '../../service/customer.service';
import { AddressService } from '../../service/address.service';
import { Address, AddressType, Coupon, Customer, Product } from '../../models';
import { ProductService } from '../../service/product.service';
import { KeycloakService } from '../../service/keycloak.service';

@Component({
  selector: 'app-deliveryAddress',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './deliveryAddress.component.html',
  styleUrls: ['./deliveryAddress.component.css'],
})
export class DeliveryAddressComponent implements OnInit, OnChanges {
  cartProducts: any[] = [];
  totalPrice: number = 0;
  appliedCoupon: Coupon | null = null;
  discountValue: number = 0;
  editAddress: Address = {} as Address;
  deliveryAddressId: number = 0;
  customer: Customer = {} as Customer;
  products: Product[] = [];
  newAddress: Address = {
    id: 0,
    street: '',
    houseNumber: 0,
    neighborhood: '',
    zipCode: 0,
    city: '',
    country: '',
    addressType: AddressType.HOME_ADDRESS,
    isActive: true
  };
  addressTypes: AddressType[] = [
    AddressType.HOME_ADDRESS,
    AddressType.BUSINES_ADDRESS,
    AddressType.SHIPPING_ADDRESS,
  ];

  // Paginação
  page = 0;
  size = this.cartProducts.length;
  sort = 'id,asc';
  totalPages = 0;
  totalElements = 0;

  constructor(
    private cartService: CartService,
    private customerService: CustomerService,
    private addressService: AddressService,
    private productService: ProductService,
    private keycloakService: KeycloakService
  ) {}

  ngOnInit(): void {
    this.loadCustomer();
    this.loadDeliveryAddress();
    this.loadPrdocuts();
    this.cartProducts = this.cartService.getCart();
    this.appliedCoupon = this.cartService.getAppliedCoupon();
    this.discountValue = this.cartService.getDiscountValue();
    this.cartService.getTotalWithDiscount().subscribe((total) => {
      this.totalPrice = total;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadCustomer();
    this.loadDeliveryAddress();
  }

  loadCustomer(): void {
    const customerId = localStorage.getItem('customerId');

    if (!customerId) {
      console.error('Nenhum ID de cliente encontrado no localStorage.');
      return;
    }

    this.customerService.findById(Number(customerId)).subscribe({
      next: (customer) => {
        this.customer = customer;
        this.loadDeliveryAddress();
      },
      error: (err) => {
        console.error('Erro ao carregar o cliente:', err);
      },
    });
  }

  insertAddress(): void {
    this.addressService
      .insert(this.newAddress, this.customer.id)
      .subscribe(() => {
        this.loadCustomer();
        this.closeModal();
      });
  }

  resetNewAddress(): void {
    this.newAddress = {
      id: 0,
      street: '',
      houseNumber: 0,
      neighborhood: '',
      zipCode: 0,
      city: '',
      country: '',
      addressType: AddressType.HOME_ADDRESS,
      isActive: true
    };
  }

  closeModal(): void {
    this.resetNewAddress();
    this.loadCustomer();
  }

  edit(address: Address): void {
    this.editAddress = { ...address };
  }

  updateAddress(): void {
    if (!this.editAddress) return;

    this.addressService
      .update(this.editAddress.id, this.customer.id, this.editAddress)
      .subscribe({
        next: (updatedAddress) => {
          const index = this.customer.addresses.findIndex(
            (p) => p.id === updatedAddress.id
          );
          if (index !== -1) {
            this.customer.addresses[index] = updatedAddress;
          }
          this.closeModal();
        },
      });
    this.loadCustomer();
  }

  loadDeliveryAddress(): void {
    const savedDeliveryAddressId = localStorage.getItem('deliveryAddressId');
    if (savedDeliveryAddressId) {
      this.deliveryAddressId = +savedDeliveryAddressId;
    }
  }

  setDeliveryAddress(id: number): void {
    this.deliveryAddressId = id;
    localStorage.setItem('deliveryAddressId', id.toString());
  }

  loadPrdocuts(): void {
    this.productService
      .findAllActive(this.page, this.totalElements, '', [])
      .subscribe((data) => {
        this.products = data.content as Product[];
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
      });
  }

  getProductById(productId: number): Product | undefined {
    return this.products.find((product) => product.id === productId);
  }

  areAllItemsValid(): boolean {
    return this.cartProducts.every(
      (item) =>
        this.getProductById(item.productId)?.sellIndicator === true &&
        this.getProductById(item.productId)?.isDeleted === false
    );
  }

  get hasAddresses(): boolean {
    return (
      Array.isArray(this.customer?.addresses) &&
      this.customer.addresses.length > 0
    );
  }

  deleteAddress(addressId: number):void{
      this.addressService.delete(addressId).subscribe(() => {
        this.loadCustomer();
        this.loadDeliveryAddress();
      });
    }
}
