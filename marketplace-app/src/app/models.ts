export enum CustomerType {
  LEGAL_PERSON = 'LEGAL_PERSON',
  NATURAL_PERSON = 'NATURAL_PERSON',
}

export interface Customer {
  id: number;
  name: string;
  email: string;
  phone: string;
  documentNumber: string;
  creditScore: string;
  isDeleted: boolean;
  customerType: CustomerType;
  addresses: Address[];
  library: Product[];
  keycloakId: string;
}

export interface Coupon {
  id: number;
  code: string;
  discountValue: number;
  discountPercentage: number;
  isActive: boolean;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  unitPrice: number;
  sellIndicator: boolean;
  isPhysical: boolean;
  isDeleted: boolean;
  categories: Category[];
  imagesUrl: string[];
  fileUrl: string;
  stock: number;
}

export interface Category {
  id: number;
  name: string;
}

export interface Address {
  id: number;
  street: string;
  houseNumber: number;
  neighborhood: string;
  city: string;
  zipCode: number;
  country: string;
  addressType: AddressType;
  isActive: boolean;
}

export enum AddressType {
  HOME_ADDRESS = 'HOME_ADDRESS',
  BUSINES_ADDRESS = 'BUSINES_ADDRESS',
  SHIPPING_ADDRESS = 'SHIPPING_ADDRESS',
}

export interface OrderItem {
  productId: number;
  quantity: number;
}

export interface OrderCoupon {
  id: number;
}

export enum OrderStatus {
  WAITING_PAYMENT = 'WAITING_PAYMENT',
  PAID = 'PAID',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELED = 'CANCELED',
}

export interface OrderRequest {
  id: number;
  orderStatus: OrderStatus;
  customerId: number;
  addressId: number;
  items: OrderItem[];
  coupon?: OrderCoupon;
}

export interface Order {
  id: number;
  moment: string;
  orderStatus: OrderStatus;
  isDeleted: boolean;
  deliveryAddress: Address;
  coupon: Coupon;
  customer: Customer;
  items: OrderItem[];
  total: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
