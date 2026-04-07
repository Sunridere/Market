import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../hooks/useCart';
import { orderService } from '../services/order.service';
import { CartItem } from '../components/cart/CartItem';
import { Button } from '../components/ui/Button';
import { Spinner } from '../components/ui/Spinner';
import toast from 'react-hot-toast';

export function CartPage() {
  const { cart, loading, clearCart, refreshCart } = useCart();
  const navigate = useNavigate();
  const [ordering, setOrdering] = useState(false);

  const handleOrder = async () => {
    setOrdering(true);
    try {
      const { data } = await orderService.create();
      await refreshCart();
      toast.success('Заказ оформлен!');
      navigate(`/orders/${data.id}`);
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { errorMessage?: string } } })
        ?.response?.data?.errorMessage ?? 'Ошибка при оформлении заказа';
      toast.error(message);
    } finally {
      setOrdering(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  const items = cart?.items ?? [];

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Корзина</h1>

      {items.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500 dark:text-gray-400 text-lg mb-4">Корзина пуста</p>
          <Link to="/catalog">
            <Button>Перейти в каталог</Button>
          </Link>
        </div>
      ) : (
        <>
          <div className="space-y-3 mb-8">
            {items.map((item) => (
              <CartItem key={item.id} item={item} />
            ))}
          </div>

          <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-6">
            <div className="flex items-center justify-between mb-4">
              <span className="text-gray-500 dark:text-gray-400">Товаров в корзине:</span>
              <span className="font-medium">
                {items.reduce((s, i) => s + i.quantity, 0)} шт.
              </span>
            </div>
            <div className="flex items-center justify-between mb-6">
              <span className="text-lg font-semibold">Итого:</span>
              <span className="text-2xl font-bold text-primary-600">
                {cart?.totalCartPrice.toLocaleString('ru-RU')} &#8381;
              </span>
            </div>
            <div className="flex gap-3">
              <Button onClick={handleOrder} disabled={ordering} size="lg" className="flex-1">
                {ordering ? 'Оформление...' : 'Оформить заказ'}
              </Button>
              <Button
                variant="ghost"
                onClick={clearCart}
                className="text-red-500 hover:text-red-600"
              >
                Очистить
              </Button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
