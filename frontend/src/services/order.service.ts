import api from './api';
import type { OrderDto } from '../types/order';
import type { Page } from '../types/page';

export const orderService = {
  create() {
    return api.post<OrderDto>('/api/v1/orders');
  },

  getAll(page = 0, size = 20) {
    return api.get<Page<OrderDto>>('/api/v1/orders', { params: { page, size } });
  },

  getById(id: string) {
    return api.get<OrderDto>(`/api/v1/orders/${id}`);
  },
};
