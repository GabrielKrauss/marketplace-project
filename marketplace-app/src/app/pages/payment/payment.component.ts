import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../service/cart.service';
import { CustomerService } from '../../service/customer.service';
import { AddressService } from '../../service/address.service';
import {
  Address,
  Coupon,
  Customer,
  OrderRequest,
  OrderStatus,
  Product,
} from '../../models';
import { OrderService } from '../../service/order.service';
import { ProductService } from '../../service/product.service';

@Component({
  selector: 'app-payment',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
})
export class PaymentComponent implements OnInit, OnChanges {
  cartProducts: any[] = [];
  totalPrice: number = 0;
  appliedCoupon: Coupon | null = null;
  discountValue: number = 0;
  editAddress: Address = {} as Address;
  deliveryAddressId: number = 0;
  customer: Customer = {} as Customer;
  orderRequest: OrderRequest = {} as OrderRequest;
  products: Product[] = [];
  showSuccessAlert: boolean = false;

  showAlert: boolean = false;
  alertMessage: string = '';

  paymentType: string = '';
  boletoCode: string = '';
  cardNumber: string = '';
  expiryDate: string = '';
  cvv: string = '';
  debitCardNumber: string = '';
  pixKey: string = '';

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
    private orderService: OrderService,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCustomer();
    this.loadPrdocuts();
    this.loadDeliveryAddress();
    this.cartProducts = this.cartService.getCart();

    this.appliedCoupon = this.cartService.getAppliedCoupon();
    this.discountValue = this.cartService.getDiscountValue();
    this.cartService.getTotalWithDiscount().subscribe((total) => {
      this.totalPrice = total;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadCustomer();
    this.loadPrdocuts();
    this.loadDeliveryAddress();
    this.cartProducts = this.cartService.getCart();

    this.appliedCoupon = this.cartService.getAppliedCoupon();
    this.discountValue = this.cartService.getDiscountValue();
    this.cartService.getTotalWithDiscount().subscribe((total) => {
      this.totalPrice = total;
    });
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
      },
      error: (err) => {
        console.error('Erro ao carregar o cliente:', err);
      },
    });
  }

  onPaymentTypeChange() {
    // Resetando os campos do formulário sempre que o tipo de pagamento mudar
    this.boletoCode = '';
    this.cardNumber = '';
    this.expiryDate = '';
    this.cvv = '';
    this.debitCardNumber = '';
    this.pixKey = '';
  }

  insertOrder(): void {
    this.orderRequest.addressId = this.deliveryAddressId;
    this.orderRequest.customerId = this.customer.id;
    this.orderRequest.items = this.cartProducts;
    this.orderRequest.orderStatus = OrderStatus.WAITING_PAYMENT;
    this.orderRequest.coupon = this.appliedCoupon ?? undefined;

    this.orderService.insert(this.orderRequest).subscribe(() => {
      this.showSuccessAlert = true;
      setTimeout(() => {
        this.router.navigate(['/home']);
        window.scrollTo(0, 0); // Redirecionamento para a página inicial
        this.cartService.clearCart();
        this.setDeliveryAddress(0);
        this.cartService.removeCoupon();
      }, 2000);
    });
  }

  loadPrdocuts(): void {
    this.productService.findAllActive(this.page, this.totalElements, '', []).subscribe((data) => {
      this.products = data.content as Product[];
      this.totalPages = data.totalPages;
      this.totalElements = data.totalElements;
    });
  }

  getProductById(productId: number): Product | undefined {
    return this.products.find((product) => product.id === productId);
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

  areAllItemsValid(): boolean {
    return this.cartProducts.every(
      (item) =>
        this.getProductById(item.productId)?.sellIndicator === true &&
        this.getProductById(item.productId)?.isDeleted === false
    );
  }
}
