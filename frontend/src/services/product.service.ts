import api from './api';
import type { ProductDto } from '../types/product';
import type { Page } from '../types/page';

export const productService = {
  getAll(page = 0, size = 20) {
    return api.get<Page<ProductDto>>('/api/v1/products', { params: { page, size } });
  },

  getById(id: string) {
    return api.get<ProductDto>(`/api/v1/products/${id}`);
  },

  create(data: ProductDto) {
    return api.post<ProductDto>('/api/v1/products', data);
  },

  update(data: ProductDto) {
    return api.put<ProductDto>('/api/v1/products', data);
  },

  delete(id: string) {
    return api.delete(`/api/v1/products/${id}`);
  },
};
