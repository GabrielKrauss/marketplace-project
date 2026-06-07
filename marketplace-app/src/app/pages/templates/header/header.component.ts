import { Component, NgModule, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../../service/cart.service';
import { KeycloakService } from '../../../service/keycloak.service';
import { CommonModule } from '@angular/common';
import { jwtDecode } from 'jwt-decode';
import { ProductService } from '../../../service/product.service';
import { Customer, Order, Product } from '../../../models';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../service/customer.service';
import { OrderService } from '../../../service/order.service';

@Component({
  selector: 'app-header',
  imports: [RouterModule, CommonModule, FormsModule],
  styleUrl: './header.component.css',
  templateUrl: './header.component.html',
})
export class HeaderComponent implements OnInit {
  cartCount: number = 0;
  dropdownOpen = false;
  searchTerm = '';
  products: Product[] = [];
  customer: Customer = {} as Customer;
  orders: Order[] = [];
  isEditingOrder = false;
  editingOrder: Order = {} as Order;
  selectedAddressId: number | null = null;

  constructor(
    private cartService: CartService,
    private keycloakService: KeycloakService,
    private productService: ProductService,
    private router: Router,
    private customerService: CustomerService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.cartService.cartCount$.subscribe((count) => {
      this.cartCount = count;
    });
    this.getCustomer();
  }

  isCustomerLogged(): boolean {
    return this.keycloakService.isAuthenticated();
  }

  isAdminLogged(): boolean {
    const keycloakInstance = this.keycloakService.getKeycloak();
    return keycloakInstance.realmAccess?.roles.includes('Admin') ?? false;
  }

  logout(): void {
    const redirectUri = window.location.href;
    localStorage.setItem('customerId', '');
    localStorage.setItem('deliveryAddressId', '');
    this.keycloakService.logout(redirectUri);
  }

  getCustomerName(): string {
    if (!this.keycloakService.isAuthenticated()) {
      return '';
    }

    const token = this.keycloakService.getToken();
    if (!token) {
      return '';
    }

    const decodedToken: any = jwtDecode(token);
    return decodedToken.completeName ?? 'Usuário';
  }

  getCustomer(): void {
    const customerId = localStorage.getItem('customerId');
    if (customerId) {
      this.customerService.findById(Number(customerId)).subscribe(
        (data) => {
          this.customer = data as Customer;
        },
        (error) => {
          console.error('Error loading client:', error);
        }
      );
    } else {
      console.warn('customerId not found in localStorage');
    }
  }

  getOrders(): void {
    this.getCustomer();
    const customerId = localStorage.getItem('customerId');
    if (customerId) {
      this.orderService.findAllActiveByCustomerId(Number(customerId)).subscribe(
        (data) => {
          this.orders = data as unknown as Order[];
          this.cancelEdit();
        },
        (error) => {
          console.error('Error loading client:', error);
        }
      );
    } else {
      console.warn('customerId not found in localStorage');
    }
  }

  startEdit(order: Order): void {
    this.editingOrder = { ...order }; // cópia para edição
    this.selectedAddressId = order.deliveryAddress?.id || null;
  }

  cancelEdit(): void {
    this.editingOrder = {} as Order;
    this.selectedAddressId = null;
  }

  saveAddressChange(): void {
    if (!this.editingOrder || !this.selectedAddressId) return;
    const selectedAddress = this.customer.addresses.find(
      (address) => address.id === Number(this.selectedAddressId)
    );

    if (!selectedAddress) return;

    this.editingOrder.deliveryAddress = selectedAddress;

    this.orderService
      .operatorUpdate(this.editingOrder.id, this.editingOrder)
      .subscribe({
        next: () => {
          this.getOrders();
          this.cancelEdit();
        },
        error: (err) => {
          console.error(err);
        },
      });
  }

  editCustomer(): void {
    this.customerService.update(this.customer.id, this.customer).subscribe(
      (response) => {
        console.log('Customer updated successfully', response);
      },
      (error) => {
        console.error('Error updating customer', error);
      }
    );
  }

  deleteCustomer(): void {
    this.customerService.delete(this.customer.id).subscribe(
      (response) => {
        this.logout();
        this.keycloakService.deleteUser(this.customer.keycloakId).subscribe(
          (response) => {
            console.log('Customer in keycloak deleted successfully', response);
          },
          (error) => {
            console.error('Error deleting customer from keycloak', error);
          }
        );
        console.log('Customer deleted successfully', response);
      },
      (error) => {
        console.error('Error deleting customer', error);
      }
    );
  }

  findProduct(): void {
    this.productService
      .findAllActive(0, 4, this.searchTerm || '', [])
      .subscribe((data) => {
        this.products = data.content as Product[];
      });
  }

  selectProduct(product: any) {
    this.router.navigate(['/product', product.id]); // Redireciona para a página do produto
    this.dropdownOpen = false;
  }

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }
}
