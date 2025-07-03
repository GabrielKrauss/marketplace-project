import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../service/product.service';
import { CategoryService } from '../../../service/category.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CartService } from '../../../service/cart.service';
import { Category, Product } from '../../../models';

@Component({
  selector: 'app-album',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, FormsModule],
  styleUrl: './album.component.css',
  templateUrl: './album.component.html',
})
export class AlbumComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  productCategories: Category[] = [];
  searchTerm: string = '';
  searchCategory: string = '';
  selectedCategories: Category[] = [];
  filteredCategoryList: Category[] = [];

  // Paginação
  page = 0;
  size = 12;
  sort = 'id,asc';
  totalPages = 0;
  totalElements = 0;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.findAllProducts();
    this.findAllCategories();
  }

  ngOnChange(): void {
    this.findAllProducts();
    this.findAllCategories();
  }

  findAllProducts(): void {
    const categoryIds = this.selectedCategories.map((c) => c.id);
    this.productService
      .findAllActive(this.page, 12, this.searchTerm || '', categoryIds)
      .subscribe((data) => {
        this.products = data.content as Product[];
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
      });
  }

  findAllCategories(): void {
    this.categoryService.findAll().subscribe((data) => {
      this.categories = data as unknown as Category[];
      this.filteredCategoryList = [...this.categories];
    });
  }

  toggleCategory(category: Category): void {
    const index = this.selectedCategories.findIndex(
      (cat) => cat.id === category.id
    );
    if (index === -1) {
      this.selectedCategories.push(category);
    } else {
      this.selectedCategories.splice(index, 1);
    }
    // Atualiza os produtos com o novo filtro de categorias
    this.onFilterChange();
  }
  
  filterCategories(): void {
    if (!this.searchCategory) {
      this.filteredCategoryList = this.categories;
    } else {
      this.filteredCategoryList = this.categories.filter((category) =>
        category.name.toLowerCase().includes(this.searchCategory.toLowerCase())
      );
    }
  }

  isSelected(category: Category): boolean {
    return this.selectedCategories.some((cat) => cat.id === category.id);
  }

  addToCart(product: Product): void {
    const existingProduct = this.cartService
      .getCart()
      .find((p) => p.productId === product.id);

    if (existingProduct) {
      if (!product.isPhysical) {
        return;
      }
      this.cartService.updateProductQuantity(
        product.id,
        existingProduct.quantity + 1
      );
    } else {
      this.cartService.addToCart(product.id);
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.findAllProducts();
      setTimeout(() => {
        const element = document.getElementById('product-album');
        if (element) {
          element.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      }, 200);
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.findAllProducts();
      setTimeout(() => {
        const element = document.getElementById('product-album');
        if (element) {
          element.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      }, 200);
    }
  }

  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages) {
      this.page = pageNumber;
      this.findAllProducts();
      setTimeout(() => {
        const element = document.getElementById('product-album');
        if (element) {
          element.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      }, 200);
    }
  }

  onFilterChange(): void {
    this.page = 0;
    this.findAllProducts();
  }
}
