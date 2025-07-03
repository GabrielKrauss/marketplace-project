import { Component, OnChanges, OnInit } from '@angular/core';
import { ProductService } from '../../service/product.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../service/category.service';
import { Category, Product } from '../../models';

@Component({
  selector: 'app-productList',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  styleUrl: './productList.component.css',
  templateUrl: './productList.component.html',
})
export class ProductListComponent implements OnInit, OnChanges {
  products: Product[] = [];
  categories: Category[] = [];
  selectedCategories: Category[] = [];
  dropdownOpen = false;
  searchTerm: string = '';
  filteredCategories: Category[] = [];
  selectedProduct: Product = {} as Product;
  newCategory: Category = {} as Category;
  newProduct: Product = {
    id: 0,
    name: '',
    description: '',
    unitPrice: 0,
    sellIndicator: false,
    isPhysical: false,
    isDeleted: false,
    categories: [],
    imagesUrl: [],
    fileUrl: '',
    stock: 0,
  };

  buttonFunctionChanges: boolean = false;

  // Paginação
  page = 0;
  size = 12;
  sort = 'id,asc';
  totalPages = 0;
  totalElements = 0;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  ngOnChanges(): void {
    this.loadCategories();
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  findAll(): void {
    this.productService
      .findAll(this.page, this.searchTerm, this.sort)
      .subscribe((data) => {
        this.products = data.content as Product[];
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.buttonFunctionChanges = true;
      });
  }

  findAllActive(): void {
    const categoryIds = this.selectedCategories.map((c) => c.id);
    this.productService
      .findAllActive(this.page, 12, this.searchTerm || '', categoryIds)
      .subscribe((data) => {
        this.products = data.content as Product[];
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.buttonFunctionChanges = false;
      });
  }

  insert(): void {
    this.productService.insert(this.newProduct).subscribe(() => {
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
      this.resetNewProduct();
      this.closeModal();
    });
  }

  edit(product: Product): void {
    this.selectedProduct = { ...product };
    this.selectedCategories = this.selectedProduct.categories;
  }

  updateProduct(): void {
    if (!this.selectedProduct) return;
    this.selectedProduct.categories = [...this.selectedCategories];

    this.productService
      .update(this.selectedProduct.id, this.selectedProduct)
      .subscribe({
        next: (updatedProduct) => {
          const index = this.products.findIndex(
            (p) => p.id === updatedProduct.id
          );
          if (index !== -1) {
            this.products[index] = updatedProduct;
          }
          this.closeModal();
        },
        error: (err) => console.error('Erro ao atualizar produto:', err),
      });
  }
  delete(productId: number): void {
    this.productService.delete(productId).subscribe(() => {
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
    });
  }

  resetNewProduct(): void {
    this.newProduct = {
      id: 0,
      name: '',
      description: '',
      unitPrice: 0,
      sellIndicator: false,
      isPhysical: false,
      isDeleted: false,
      categories: [],
      imagesUrl: [],
      fileUrl: '',
      stock: 0,
    };
    this.selectedCategories = [];
  }

  loadCategories(): void {
    this.categoryService.findAll().subscribe((data) => {
      this.categories = data as unknown as Category[];
    });
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
    if (this.dropdownOpen) {
      this.searchTerm = '';
      this.filterCategories();
    }
  }

  isSelected(category: Category): boolean {
    return this.selectedCategories.some((cat) => cat.id === category.id);
  }

  toggleCategory(category: Category): void {
    const index = this.selectedCategories.findIndex(
      (cat) => cat.id === category.id
    );

    if (index !== -1) {
      this.selectedCategories.splice(index, 1);
    } else {
      this.selectedCategories.push(category);
    }

    this.newProduct.categories = [...this.selectedCategories];
  }

  filterCategories(): void {
    if (!this.searchTerm) {
      this.filteredCategories = this.categories;
    } else {
      this.filteredCategories = this.categories.filter((category) =>
        category.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }

  createCategory(newCategoryName: string): void {
    this.newCategory = {
      id: 0,
      name: newCategoryName,
    };
    this.categoryService.insert(this.newCategory).subscribe(() => {
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
      this.loadCategories();
      this.searchTerm = '';
      this.filterCategories();
    });
  }

  openEditModal(product: Product): void {
    this.selectedProduct = { ...product };
    this.selectedCategories = { ...product.categories };
    this.dropdownOpen = false;
    this.searchTerm = ''; // Limpa o termo de pesquisa
    this.dropdownOpen = false;
  }

  openAddModal(): void {
    this.selectedCategories = [];
    this.dropdownOpen = false;
    this.searchTerm = ''; // Limpa o termo de pesquisa
    this.dropdownOpen = false;
  }

  closeModal(): void {
    this.selectedCategories = [];
    this.dropdownOpen = false;
    this.searchTerm = ''; // Limpa o termo de pesquisa
    this.dropdownOpen = false;
    this.resetNewProduct();
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  reactivateProduct(product: Product): void {
    if (product.isDeleted) {
      const updatedProduct = { ...product, isDeleted: false };

      this.productService.update(product.id, updatedProduct).subscribe({
        next: () => {
          product.isDeleted = false;
          this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
          console.log(`Produto ${product.id} reativado com sucesso.`);
        },
        error: (err) => console.error('Erro ao reativar o produto:', err),
      });
    }
  }

  addImageUrl(product: Product) {
    product.imagesUrl.push(''); // Adiciona um novo campo vazio
  }

  removeImageUrl(product: Product, index: number) {
    product.imagesUrl.splice(index, 1);
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
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
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
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
      this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
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
    this.buttonFunctionChanges ? this.findAll() : this.findAllActive();
  }

  validateStock() {
    if (this.newProduct.stock == null || this.newProduct.stock < 0) {
      this.newProduct.stock = 0;
    }

    if (this.newProduct.stock <= 0) {
      this.newProduct.sellIndicator = false;
    }

    if (this.selectedProduct.stock == null || this.selectedProduct.stock < 0) {
      this.selectedProduct.stock = 0;
    }

    if (this.selectedProduct.stock <= 0) {
      this.selectedProduct.sellIndicator = false;
    }
  }

  onPhysicalChange() {
    if (!this.newProduct.isPhysical) {
      this.newProduct.stock = 0;
      this.newProduct.sellIndicator = false;
    }
    if (!this.selectedProduct.isPhysical) {
      this.selectedProduct.stock = 0;
      this.selectedProduct.sellIndicator = false;
    }
  }
}
