export type OrderStatus =
  | 'CREATED'
  | 'PAID'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'COMPLETED'
  | 'CANCELLED';

export interface OrderItemDto {
  id: string;
  productId: string;
  name: string;
  quantity: number;
  priceAtPurchase: number;
  totalPriceAtPurchase: number;
}

export interface OrderDto {
  id: string;
  status: OrderStatus;
  items: OrderItemDto[];
  totalOrderPrice: number;
  createdAt: string;
  updatedAt: string;
  deliveryAddress: string;
}
