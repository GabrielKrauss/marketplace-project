import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin, map, Observable } from 'rxjs';
import { Coupon, OrderItem, Product } from '../models';
import { ProductService } from './product.service';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private cartKey = 'cart';
  private couponKey = 'appliedCoupon';
  private discountKey = 'discountValue';
  private cart: OrderItem[] = [];
  private appliedCoupon: Coupon | null = null;
  private discountValue: number = 0;

  private cartCountSubject = new BehaviorSubject<number>(0);
  cartCount$ = this.cartCountSubject.asObservable();

  constructor(private productService: ProductService) {
    this.loadCart();
    this.loadAppliedCoupon();
  }

  private loadCart(): void {
    const storedCart = localStorage.getItem(this.cartKey);
    this.cart = storedCart ? JSON.parse(storedCart) : [];
    this.updateCartCount();
  }

  private loadAppliedCoupon(): void {
    const storedCoupon = localStorage.getItem(this.couponKey);
    const storedDiscount = localStorage.getItem(this.discountKey);

    this.appliedCoupon = storedCoupon ? JSON.parse(storedCoupon) : null;
    this.discountValue = storedDiscount ? parseFloat(storedDiscount) : 0;
  }

  private saveAppliedCoupon(): void {
    if (this.appliedCoupon) {
      localStorage.setItem(this.couponKey, JSON.stringify(this.appliedCoupon));
      localStorage.setItem(this.discountKey, this.discountValue.toString());
    } else {
      localStorage.removeItem(this.couponKey);
      localStorage.removeItem(this.discountKey);
    }
  }

  private saveCart(): void {
    localStorage.setItem(this.cartKey, JSON.stringify(this.cart));
    this.updateCartCount();
  }

  private updateCartCount(): void {
    const totalItems = this.cart.reduce((sum, item) => sum + item.quantity, 0);
    this.cartCountSubject.next(totalItems);
  }

  getCart(): OrderItem[] {
    return [...this.cart];
  }

  addToCart(productId: number): void {
    const existingProduct = this.cart.find((p) => p.productId === productId);

    if (existingProduct) {
      existingProduct.quantity += 1;
    } else {
      this.cart.push({ productId, quantity: 1 });
    }

    this.saveCart();
    this.updateCartCount();
    this.calculateDiscount();
    this.saveAppliedCoupon();
  }

  updateProductQuantity(productId: number, quantity: number): void {
    const product = this.cart.find((p) => p.productId === productId);

    if (product) {
      if (quantity > 0) {
        product.quantity = quantity;
      } else {
        this.cart = this.cart.filter((p) => p.productId !== productId);
      }

      this.saveCart();
      this.updateCartCount();
    }
    this.calculateDiscount();
    this.saveAppliedCoupon();
  }

  removeFromCart(productId: number): void {
    this.cart = this.cart.filter((p) => p.productId !== productId);
    this.saveCart();
    this.updateCartCount();
  }

  clearCart(): void {
    this.cart = [];
    this.saveCart();
    this.updateCartCount();
  }

  getTotalPrice(): Observable<number> {
    const productRequests = this.cart.map((item) =>
      this.productService.findById(item.productId).pipe(
        map((product) => product?.unitPrice ?? 0)
      )
    );

    // Usando forkJoin para esperar todas as requisições
    return forkJoin(productRequests).pipe(
      map((prices) => {
        return prices.reduce(
          (total, price, index) => total + price * this.cart[index].quantity,
          0
        );
      })
    );
  }


  getAppliedCoupon(): Coupon | null {
    return this.appliedCoupon;
  }

  getDiscountValue(): number {
    return this.discountValue;
  }

  applyCoupon(coupon: Coupon): void {
    this.appliedCoupon = coupon;
    this.calculateDiscount();
    this.saveAppliedCoupon();
  }

  removeCoupon(): void {
    this.appliedCoupon = null;
    this.discountValue = 0;
    this.saveAppliedCoupon();
  }

  private calculateDiscount(): void {
    this.getTotalPrice().subscribe((subtotal) => {
      if (this.appliedCoupon?.isActive) {
        if (this.appliedCoupon.discountPercentage > 0) {
          this.discountValue = subtotal * (this.appliedCoupon.discountPercentage / 100);
        } else if (this.appliedCoupon.discountValue > 0) {
          this.discountValue = this.appliedCoupon.discountValue;
        }
      } else {
        this.discountValue = 0;
      }
  
      localStorage.setItem(this.discountKey, this.discountValue.toString());
    });
  }
  

  getTotalWithDiscount(): Observable<number> {
    return this.getTotalPrice().pipe(
      map((totalPrice) => Math.max(0, totalPrice - this.discountValue))
    );
  }

  addToCartProduct(productId: number, quantity: number): void{
    const existingProduct = this.cart.find((p) => p.productId === productId);

    if (existingProduct) {
      existingProduct.quantity += 1;
    } else {
      this.cart.push({ productId, quantity: quantity });
    }

    this.saveCart();
    this.updateCartCount();
    this.calculateDiscount();
    this.saveAppliedCoupon();
  }

}
