import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CartService } from '../../service/cart.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CouponService } from '../../service/coupon.service';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../service/customer.service';
import { Coupon, Customer, OrderItem, Product } from '../../models';
import { ProductService } from '../../service/product.service';
import { forkJoin, map } from 'rxjs';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit, OnChanges {
  cartProducts: OrderItem[] = [];
  totalPrice: number = 0;

  couponCode: string = '';
  couponError: string = '';
  appliedCoupon: Coupon | null = null;
  discountValue: number = 0;
  customer: Customer = {} as Customer;
  library: Product[] = [];
  products: Product[] = [];

  showLoginModal: boolean = false;
  // Paginação
  page = 0;
  size = this.cartProducts.length;
  sort = 'id,asc';
  totalPages = 0;
  totalElements = 0;

  constructor(
    private cartService: CartService,
    private couponService: CouponService,
    private customerService: CustomerService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.loadCart();
    this.loadAppliedCoupon();
    this.loadCustomer();
    this.loadProducts();
    this.calculateTotalPrice();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.calculateTotalPrice();
    this.loadProducts();
    this.loadCustomer();
  }

  loadCart(): void {
    this.cartProducts = this.cartService.getCart();
    this.calculateTotalPrice();
  }

  loadAppliedCoupon(): void {
    const storedCoupon = this.cartService.getAppliedCoupon();
    if (storedCoupon) {
      this.appliedCoupon = storedCoupon;
      this.calculateTotalPrice();
    }
  }

  removeFromCart(productId: number): void {
    this.cartService.removeFromCart(productId);
    this.loadCart();
  }

  updateQuantity(event: Event, productId: number): void {
    const inputElement = event.target as HTMLInputElement;
    const quantity = inputElement.valueAsNumber;

    if (!isNaN(quantity) && quantity >= 0) {
      this.cartService.updateProductQuantity(productId, quantity);
      this.loadCart();
    }
  }

  applyCoupon(): void {
    this.couponService.findByCode(this.couponCode).subscribe({
      next: (foundCoupon) => {
        if (!foundCoupon) {
          this.couponError = 'Invalid coupon!';
          this.appliedCoupon = null;
          this.discountValue = 0;
          this.cartService.removeCoupon();
        } else if (!foundCoupon.isActive) {
          this.couponError = 'Expired coupon!';
          this.appliedCoupon = null;
          this.discountValue = 0;
          this.cartService.removeCoupon();
        } else {
          this.appliedCoupon = foundCoupon;
          this.couponError = '';
          this.cartService.applyCoupon(foundCoupon);
        }

        this.calculateTotalPrice();
      },
      error: () => {
        this.couponError = 'Invalid coupon!';
      },
    });
  }

  calculateTotalPrice(): void {
    this.totalPrice = 0;
    this.discountValue = 0;
    let tempTotal = 0;

    const productRequests = this.cartProducts.map((item) =>
      this.productService
        .findById(item.productId)
        .pipe(map((product) => product.unitPrice ?? 0))
    );

    forkJoin(productRequests).subscribe((unitPrices) => {
      unitPrices.forEach((unitPrice, index) => {
        const quantity = this.cartProducts[index].quantity;
        tempTotal += unitPrice * quantity;
      });

      if (this.appliedCoupon?.isActive) {
        if (this.appliedCoupon.discountPercentage > 0) {
          this.discountValue =
            tempTotal * (this.appliedCoupon.discountPercentage / 100);
        } else if (this.appliedCoupon.discountValue > 0) {
          this.discountValue = this.appliedCoupon.discountValue;
        }

        tempTotal -= this.discountValue;
      }

      this.totalPrice = Math.max(0, tempTotal);
    });
  }

  loadCustomer(): void {
    const customerId = localStorage.getItem('customerId');

    if (!customerId) {
      this.customer = {} as Customer;
      console.error('Nenhum ID de cliente encontrado no localStorage.');
      return;
    }

    this.customerService.findById(Number(customerId)).subscribe({
      next: (customer) => {
        this.customer = customer;
        this.library = customer.library;
        console.log('Cliente carregado:', this.customer); // Verifique se o cliente foi carregado corretamente
      },
      error: (err) => {
        console.error('Erro ao carregar o cliente:', err);
      },
    });
  }

  checkCustomerLogged(): boolean {
    const customerId = localStorage.getItem('customerId');
    return customerId !== null && customerId.trim() !== '';
  }

  closeModal() {
    this.showLoginModal = false;
  }

  loadProducts(): void {
    this.productService
      .findAllActive(this.page, this.totalElements, '', [])
      .subscribe((data) => {
        this.products = data.content as Product[];
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
      });
  }

  checkIfProductInLibrary(cartProductId: number): boolean {
    return this.library.some(
      (product) =>
        product.id === cartProductId &&
        !this.getProductById(cartProductId)?.isPhysical
    );
  }

  get isProductInLibrary(): boolean {
    return this.cartProducts.some((cartProduct) =>
      this.library.some(
        (product) =>
          product.id === cartProduct.productId &&
          !product.isPhysical &&
          !this.getProductById(cartProduct.productId)?.isPhysical
      )
    );
  }

  getProductById(id: number): Product | undefined {
    return this.products.find((product) => product.id === id);
  }

  clearCoupon(): void {
    this.appliedCoupon = null;
    this.couponError = '';
    this.discountValue = 0;
    this.cartService.removeCoupon(); // Certifique-se de que esta função remove o cupom no serviço
    this.calculateTotalPrice(); // Recalcula o total após limpar o cupom
  }

  areAllItemsValid(): boolean {
    return this.cartProducts.every(
      (item) =>
        this.getProductById(item.productId)?.sellIndicator === true &&
        this.getProductById(item.productId)?.isDeleted === false
    );
  }
}
