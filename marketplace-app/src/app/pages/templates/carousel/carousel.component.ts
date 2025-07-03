import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { Product } from '../../../models';
import { ProductService } from '../../../service/product.service';
import { CategoryService } from '../../../service/category.service';

@Component({
  selector: 'app-carousel',
  imports: [RouterModule, CommonModule],
  styleUrl: './carousel.component.css',
  templateUrl: './carousel.component.html',
})
export class CarouselComponent {
  products: Product[] = [];
  productsCarousel: Product[] = [];

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.findAllProducts();
    this.findProducts();
  }

  ngOnChange(): void {
    this.findAllProducts();
    this.findProducts();
  }

  findAllProducts(): void {
    this.productService.findAllActive(0, 3, '', [1, 2, 3]).subscribe((data) => {
      this.productsCarousel = data.content as Product[];
    });
  }

  findProducts(): void {
    this.productService.findAllActive(0, 100, '', []).subscribe((data) => {
      const allProducts = data.content as Product[];
      this.products = allProducts.sort(() => Math.random() - 0.5).slice(0, 3);
    });
  }
}
