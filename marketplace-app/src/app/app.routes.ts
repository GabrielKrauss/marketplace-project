import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LibraryComponent } from './pages/library/library.component';
import { ProductComponent } from './pages/product/product.component';
import { LoginComponent } from './pages/login/login.component';
import { SignupComponent } from './pages/signup/signup.component';
import { CartComponent } from './pages/cart/cart.component';
import { DashboardComponent } from './admin/dashboard/dashboard.component';
import { ProductListComponent } from './admin/productList/productList.component';
import { DeliveryAddressComponent } from './pages/deliveryAddress/deliveryAddress.component';
import { PaymentComponent } from './pages/payment/payment.component';
import { OrderListComponent } from './admin/orderList/orderList.component';
import { AuthGuard } from './guard/auth.guard';
import { AdminGuard } from './guard/admin.guard';
import { AccessDeniedComponent } from './pages/accessDenied/accessDenied.component';
import { CustomerListComponent } from './admin/customerList/customerList.component';
import { CouponListComponent } from './admin/couponList/couponList.component';
import { CategoryListComponent } from './admin/categoryList/categoryList.component';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'library', component: LibraryComponent },
  { path: 'product/:productId', component: ProductComponent },
  { path: 'cart', component: CartComponent },
  { path: 'deliveryAddress', component: DeliveryAddressComponent },
  { path: 'payment', component: PaymentComponent },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'signup', component: SignupComponent },
  { path: 'access-denied', component: AccessDeniedComponent},
  { path: 'dashboard', component: DashboardComponent, canActivate: [AdminGuard], children:[
    { path: 'categoryList', component: CategoryListComponent },
    { path: 'couponList', component: CouponListComponent },
    { path: 'customerList', component: CustomerListComponent },
    { path: 'orderList', component: OrderListComponent },
    { path: 'productList', component: ProductListComponent }
  ]},
];
