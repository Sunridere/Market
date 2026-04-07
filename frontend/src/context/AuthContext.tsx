import { createContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import { jwtDecode } from 'jwt-decode';
import { userService } from '../services/user.service';
import { authService } from '../services/auth.service';
import type { UserDto } from '../types/user';
import type { JwtPayload } from '../types/auth';

interface AuthContextType {
  user: UserDto | null;
  token: string | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  loading: boolean;
  login: (accessToken: string, refreshToken: string) => Promise<void>;
  logout: () => void;
  updateUser: (user: UserDto) => void;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  token: null,
  isAuthenticated: false,
  isAdmin: false,
  loading: true,
  login: async () => {},
  logout: () => {},
  updateUser: () => {},
});

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserDto | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('accessToken'));
  const [loading, setLoading] = useState(true);

  const isAuthenticated = !!token && !!user;

  const isAdmin = (() => {
    if (!token) return false;
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      return decoded.role === 'ROLE_ADMIN';
    } catch {
      return false;
    }
  })();

  const logout = useCallback(async () => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
      try {
        await authService.logout(refreshToken);
      } catch {
        // ignore — token may already be revoked
      }
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setToken(null);
    setUser(null);
  }, []);

  const login = useCallback(async (accessToken: string, refreshToken: string) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    setToken(accessToken);
    try {
      const { data } = await userService.getMe();
      setUser(data);
    } catch {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      setToken(null);
    }
  }, []);

  const updateUser = useCallback((updatedUser: UserDto) => {
    setUser(updatedUser);
  }, []);

  useEffect(() => {
    const init = async () => {
      if (token) {
        try {
          const decoded = jwtDecode<JwtPayload>(token);
          if (decoded.exp * 1000 < Date.now()) {
            // Access token expired — try to refresh
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
              try {
                const { data } = await authService.refresh(refreshToken);
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);
                setToken(data.accessToken);
                const { data: userData } = await userService.getMe();
                setUser(userData);
              } catch {
                logout();
              }
            } else {
              logout();
            }
          } else {
            const { data } = await userService.getMe();
            setUser(data);
          }
        } catch {
          logout();
        }
      }
      setLoading(false);
    };
    init();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <AuthContext.Provider
      value={{ user, token, isAuthenticated, isAdmin, loading, login, logout, updateUser }}
    >
      {children}
    </AuthContext.Provider>
  );
}
