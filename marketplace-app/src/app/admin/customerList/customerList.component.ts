import { Component, OnChanges, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Customer, CustomerType } from '../../models';
import { CustomerService } from '../../service/customer.service';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-customerList',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
  styleUrl: './customerList.component.css',
  templateUrl: './customerList.component.html',
})
export class CustomerListComponent implements OnInit, OnChanges {
  customers: Customer[] = [];
  dropdownOpen = false;
  searchTerm: string = '';
  selectedCustomer: Customer = {} as Customer;
  newCustomer: Customer = {
    id: 0,
    name: '',
    email: '',
    phone: '',
    documentNumber: '',
    creditScore: '',
    isDeleted: false,
    customerType: CustomerType.LEGAL_PERSON,
    addresses: [],
    library: [],
    keycloakId: '',
  };
  filteredCustomerList: Customer[] = [];
  buttonFunctionChanges: boolean = false;
  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }
  ngOnChanges(): void {
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  findAll(): void {
    this.customerService.findAllCustomers().subscribe((data) => {
      this.customers = data as unknown as Customer[];
      this.buttonFunctionChanges = true;
      this.filteredCustomerList = [...this.customers];
    });
  }

  findAllActive(): void {
    this.customerService.findAllActiveCustomers().subscribe((data) => {
      this.customers = data as unknown as Customer[];
      this.buttonFunctionChanges = false;
      this.filteredCustomerList = [...this.customers];
    });
  }

  edit(customer: Customer): void {
    this.selectedCustomer = { ...customer };
  }

  updateCustomer(): void {
    if (!this.selectedCustomer) return;

    this.customerService
      .update(this.selectedCustomer.id, this.selectedCustomer)
      .subscribe({
        next: (updatedOrder) => {
          const index = this.customers.findIndex(
            (p) => p.id === updatedOrder.id
          );
          if (index !== -1) {
            this.customers[index] = updatedOrder;
          }
          this.closeModal();
        },
        error: (err) => console.error('Erro ao atualizar produto:', err),
      });
  }

  delete(customerId: number): void {
    this.customerService.delete(customerId).subscribe(() => {
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
    });
  }

  resetNewOrder(): void {
    this.newCustomer = {
      id: 0,
      name: '',
      email: '',
      phone: '',
      documentNumber: '',
      creditScore: '',
      isDeleted: false,
      customerType: CustomerType.LEGAL_PERSON,
      addresses: [],
      library: [],
      keycloakId: '',
    };
  }

  openEditModal(customer: Customer): void {
    this.selectedCustomer = { ...customer };
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

  filterCustomers(): void {
    if (!this.searchTerm) {
      this.filteredCustomerList = this.customers;
    } else {
      this.filteredCustomerList = this.customers.filter((customer) =>
        customer.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }
}
