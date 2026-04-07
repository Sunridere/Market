import api from './api';

export const fileService = {
  upload(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return api.post<{ url: string }>('/api/v1/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
