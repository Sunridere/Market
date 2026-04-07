import api from './api';
import type { SignUpRequest, SignInRequest, JwtAuthenticationResponse } from '../types/auth';

export const authService = {
  signUp(data: SignUpRequest) {
    return api.post<JwtAuthenticationResponse>('/auth/sign-up', data);
  },

  signIn(data: SignInRequest) {
    return api.post<JwtAuthenticationResponse>('/auth/sign-in', data);
  },

  refresh(refreshToken: string) {
    return api.post<JwtAuthenticationResponse>('/auth/refresh', { refreshToken });
  },

  logout(refreshToken: string) {
    return api.post('/auth/logout', { refreshToken });
  },
};
