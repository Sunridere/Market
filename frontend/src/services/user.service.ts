import api from './api';
import type { UserDto } from '../types/user';
import type { Page } from '../types/page';

export const userService = {
  getMe() {
    return api.get<UserDto>('/api/v1/user/me');
  },

  updateMe(data: UserDto) {
    return api.put<UserDto>('/api/v1/user/me', data);
  },

  getAll(page = 0, size = 20) {
    return api.get<Page<UserDto>>('/api/v1/user', { params: { page, size } });
  },

  blockUser(id: string) {
    return api.patch(`/api/v1/user/block/${id}`);
  },
};
