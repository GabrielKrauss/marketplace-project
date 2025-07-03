import { Component, inject, OnInit } from '@angular/core';
import { ProductService } from '../../service/product.service';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Product } from '../../models';
import { CartService } from '../../service/cart.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product',
  imports: [CommonModule, FormsModule],
  styleUrl: './product.component.css',
  templateUrl: './product.component.html',
})
export class ProductComponent implements OnInit{
  private productService = inject(ProductService);
  private route = inject(ActivatedRoute);
  selectedImage: string = "https://dummyimage.com/500x500/dee2e6/6c757d.jpg";

  quantity: number = 1;
  product: Product = {} as Product;
  relatedProducts: Product[] = [];

  constructor(private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.route.paramMap.subscribe((params: ParamMap) => {
      const idParam = params.get('productId');
      const id = idParam ? Number(idParam) : NaN;
      this.findById(id);
      
    });
    
  }

  findById(id: number): void {
    this.productService.findById(id).subscribe((data) => {
      this.product = data as unknown as Product;
      if (this.product.imagesUrl?.length > 0) {
        this.selectedImage = this.product.imagesUrl[0];
      }
      this.findRelatedProducts();
    });
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
        existingProduct.quantity + this.quantity
      );
    } else {
      this.cartService.addToCartProduct(product.id, this.quantity);
    }
  }

  updateQuantity(value: number) {
    this.quantity = Math.max(1, value);
  }

  changeImage(image: string) {
    this.selectedImage = image; // Atualizar a imagem principal
  }

  findRelatedProducts(): void{
    const categoryIds = this.product.categories ? this.product.categories.map(c => c.id) : [];
    this.productService
      .findAllActive(0, 100, '', categoryIds)
      .subscribe((data) => {
        const allProducts = (data.content as Product[])
          .filter(prod => prod.id !== this.product.id);
        
        this.relatedProducts = allProducts
          .sort(() => Math.random() - 0.5)
          .slice(0, 4);
      });
  }

  navigateToProduct(productId: number): void {
    // Redireciona para a página de detalhes do produto.
    this.router.navigate(['/product', productId]);
  }
}
