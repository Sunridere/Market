export interface CartItemDto {
  id: string;
  productId: string;
  name: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface CartDto {
  id: string;
  items: CartItemDto[];
  totalCartPrice: number;
}

export interface ItemRequestDto {
  productId: string;
  quantity: number;
}

export interface UpdateQuantityRequestDto {
  quantity: number;
}
