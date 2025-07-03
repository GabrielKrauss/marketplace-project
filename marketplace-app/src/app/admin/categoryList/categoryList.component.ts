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
  selector: 'app-categoryList',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
  styleUrl: './categoryList.component.css',
  templateUrl: './categoryList.component.html',
})
export class CategoryListComponent implements OnInit, OnChanges {
  categories: Category[] = [];
  dropdownOpen = false;
  searchTerm: string = '';
  selectedCategory: Category = {} as Category;
  newCategory: Category = {
    id: 0,
    name: ''
  };
  filteredCategoryList: Category[] = [];

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.findAll();
  }
  ngOnChanges(): void {
    this.findAll();
  }

  findAll(): void {
    this.categoryService.findAll().subscribe((data) => {
      this.categories = data as unknown as Category[];
      this.filteredCategoryList = [...this.categories];
    });
  }

  edit(category: Category): void {
    this.selectedCategory = { ...category };
  }

  updateCategory(): void {
    if (!this.selectedCategory) return;

    this.categoryService
      .update(this.selectedCategory.id, this.selectedCategory)
      .subscribe({
        next: (updatedCategory) => {
          const index = this.categories.findIndex(
            (p) => p.id === updatedCategory.id
          );
          if (index !== -1) {
            this.categories[index] = updatedCategory;
          }
          this.closeModal();
        },
        error: (err) => console.error('Erro ao atualizar category:', err),
      });
  }
  
  delete(productId: number): void {
    this.categoryService.delete(productId).subscribe(() => {
      this.findAll();
    });
  }

  insert(): void {
    this.categoryService.insert(this.newCategory).subscribe(() => {
      this.findAll();
      this.resetNewCategory();
      this.closeModal();
    });
  }


  resetNewCategory(): void {
    this.newCategory = {
      id: 0,
      name: ''
    };
  }

  openEditModal(category: Category): void {
    this.selectedCategory = { ...category };
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
    this.resetNewCategory();
    this.findAll();
  }

  filterCategories(): void {
    if (!this.searchTerm) {
      this.filteredCategoryList = this.categories;
    } else {
      this.filteredCategoryList = this.categories.filter((category) =>
        category.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }
}
