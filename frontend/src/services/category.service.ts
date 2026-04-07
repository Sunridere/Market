import api from './api';
import type { CategoryDto } from '../types/product';

export const categoryService = {
  getAll() {
    return api.get<CategoryDto[]>('/api/v1/categories');
  },

  create(data: { name: string }) {
    return api.post<CategoryDto>('/api/v1/categories', data);
  },
};
