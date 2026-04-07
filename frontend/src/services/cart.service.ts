import api from './api';
import type { CartDto, ItemRequestDto, UpdateQuantityRequestDto } from '../types/cart';

export const cartService = {
  getCart() {
    return api.get<CartDto>('/api/v1/cart');
  },

  addItem(data: ItemRequestDto) {
    return api.post<CartDto>('/api/v1/cart/items', data);
  },

  updateQuantity(productId: string, data: UpdateQuantityRequestDto) {
    return api.put<CartDto>(`/api/v1/cart/items/${productId}`, data);
  },

  removeItem(productId: string) {
    return api.delete<CartDto>(`/api/v1/cart/items/${productId}`);
  },

  clearCart() {
    return api.delete<CartDto>('/api/v1/cart/clear');
  },
};
