import { createContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import { cartService } from '../services/cart.service';
import { useAuth } from '../hooks/useAuth';
import type { CartDto } from '../types/cart';
import toast from 'react-hot-toast';

interface CartContextType {
  cart: CartDto | null;
  loading: boolean;
  itemCount: number;
  addToCart: (productId: string, quantity: number) => Promise<void>;
  updateQuantity: (productId: string, quantity: number) => Promise<void>;
  removeItem: (productId: string) => Promise<void>;
  clearCart: () => Promise<void>;
  refreshCart: () => Promise<void>;
}

export const CartContext = createContext<CartContextType>({
  cart: null,
  loading: false,
  itemCount: 0,
  addToCart: async () => {},
  updateQuantity: async () => {},
  removeItem: async () => {},
  clearCart: async () => {},
  refreshCart: async () => {},
});

export function CartProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const [cart, setCart] = useState<CartDto | null>(null);
  const [loading, setLoading] = useState(false);

  const itemCount = cart?.items.reduce((sum, item) => sum + item.quantity, 0) ?? 0;

  const refreshCart = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    try {
      const { data } = await cartService.getCart();
      setCart(data);
    } catch {
      // silent fail
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    if (isAuthenticated) {
      refreshCart();
    } else {
      setCart(null);
    }
  }, [isAuthenticated, refreshCart]);

  const addToCart = useCallback(async (productId: string, quantity: number) => {
    try {
      const { data } = await cartService.addItem({ productId, quantity });
      setCart(data);
      toast.success('Товар добавлен в корзину');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { errorMessage?: string } } })
        ?.response?.data?.errorMessage ?? 'Ошибка при добавлении в корзину';
      toast.error(message);
    }
  }, []);

  const updateQuantity = useCallback(async (productId: string, quantity: number) => {
    try {
      const { data } = await cartService.updateQuantity(productId, { quantity });
      setCart(data);
    } catch {
      toast.error('Ошибка при обновлении количества');
    }
  }, []);

  const removeItem = useCallback(async (productId: string) => {
    try {
      const { data } = await cartService.removeItem(productId);
      setCart(data);
      toast.success('Товар удалён из корзины');
    } catch {
      toast.error('Ошибка при удалении товара');
    }
  }, []);

  const clearCart = useCallback(async () => {
    try {
      const { data } = await cartService.clearCart();
      setCart(data);
    } catch {
      toast.error('Ошибка при очистке корзины');
    }
  }, []);

  return (
    <CartContext.Provider
      value={{ cart, loading, itemCount, addToCart, updateQuantity, removeItem, clearCart, refreshCart }}
    >
      {children}
    </CartContext.Provider>
  );
}
