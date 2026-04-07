export interface SignUpRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

export interface SignInRequest {
  username: string;
  password: string;
}

export interface JwtAuthenticationResponse {
  accessToken: string;
  refreshToken: string;
}

export interface JwtPayload {
  sub: string;
  id: string;
  email: string;
  role: string;
  iat: number;
  exp: number;
}
