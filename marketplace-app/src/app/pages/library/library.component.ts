import { Component, OnInit } from '@angular/core';
import { Category, Product } from '../../models';
import { CategoryService } from '../../service/category.service';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from '../../service/keycloak.service';
import { jwtDecode } from 'jwt-decode';
import { CustomerService } from '../../service/customer.service';
import { FileDownloadService } from '../../service/fileDownload.service';

@Component({
  selector: 'app-library',
  imports: [FormsModule, RouterModule, CommonModule],
  styleUrl: './library.component.css',
  templateUrl: './library.component.html',
})
export class LibraryComponent implements OnInit {
  products: Product[] = [];
  allProducts: Product[] = [];
  categories: Category[] = [];
  productCategories: Category[] = [];
  searchTerm: string = '';
  searchCategory: string = '';
  filteredProductsList: Product[] = [];
  selectedCategories: Category[] = [];
  filteredCategoryList: Category[] = [];

  customerLogged: boolean = false;

  constructor(
    private categoryService: CategoryService,
    private customerService: CustomerService,
    private keycloakService: KeycloakService,
    private fileDownloadService: FileDownloadService
  ) {}

  ngOnInit(): void {
    this.getCustomerLibrary();
    this.findAllCategories();
  }

  ngOnChange(): void {
    this.getCustomerLibrary();
    this.findAllCategories();
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
    this.onFilterChange();
    // Atualiza os produtos com o novo filtro de categorias
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

  getCustomerLibrary(): void {
    // Verifica se o usuário está autenticado
    if (!this.keycloakService.isAuthenticated()) {
      this.customerLogged = false;
      console.log('Usuário não autenticado.');
      return;
    }

    this.customerLogged = true;
    const token = this.keycloakService.getToken();

    // Verifica se o token foi obtido
    if (!token) {
      this.customerLogged = false;
      console.log('Token não retornado pelo KeycloakService.');
      return;
    }

    console.log('Token recebido:', token);

    // Tenta decodificar o token
    let decodedToken: any;
    try {
      decodedToken = jwtDecode(token);
    } catch (error) {
      console.error('Erro ao decodificar o token:', error);
      this.customerLogged = false;
      return;
    }

    console.log('Token decodificado:', decodedToken);

    // Verifica se a propriedade keycloakId existe no token decodificado
    // Algumas vezes o identificador pode vir como "sub" ou outra propriedade
    const keycloakId = decodedToken.keycloakId || decodedToken.sub;
    if (!keycloakId) {
      console.error('Não foi encontrado keycloakId no token decodificado.');
      this.customerLogged = false;
      return;
    }

    // Busca o customer com base no keycloakId
    this.customerService.findByKeycloakId(keycloakId).subscribe(
      (data) => {
        console.log('Dados do cliente recebidos:', data);
        this.products = (data?.library as Product[]) || [];
        this.filteredProductsList = [...this.products];
      },
      (error) => {
        console.error('Erro ao buscar a biblioteca do cliente:', error);
      }
    );
  }

  onFilterChange(): void {
    const term = this.searchTerm?.toLowerCase() || '';

    let filtered = this.products.filter(
      (product) =>
        product.name?.toLowerCase().includes(term)
    );

    if (this.selectedCategories.length > 0) {
      filtered = filtered.filter((product) =>
        product.categories?.some((cat) =>
          this.selectedCategories.some((sel) => sel.name === cat.name)
        )
      );
    }

    this.filteredProductsList = filtered;
  }

  download(fileUrl: string, fileName: string) {
    this.fileDownloadService.downloadRawTextAsTxt(fileUrl, fileName + '.txt');
  }
}
