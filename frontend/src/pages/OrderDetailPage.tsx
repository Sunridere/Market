import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { orderService } from '../services/order.service';
import { Spinner } from '../components/ui/Spinner';
import type { OrderDto } from '../types/order';

const statusLabels: Record<string, string> = {
  CREATED: 'Создан',
  PAID: 'Оплачен',
  SHIPPED: 'Отправлен',
  DELIVERED: 'Доставлен',
  COMPLETED: 'Завершён',
  CANCELLED: 'Отменён',
};

const statusColors: Record<string, string> = {
  CREATED: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
  PAID: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
  SHIPPED: 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200',
  DELIVERED: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
  COMPLETED: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
  CANCELLED: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
};

export function OrderDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [order, setOrder] = useState<OrderDto | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    orderService
      .getById(id)
      .then(({ data }) => setOrder(data))
      .catch(() => setOrder(null))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!order) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-20 text-center">
        <h1 className="text-2xl font-bold mb-4">Заказ не найден</h1>
        <Link to="/orders" className="text-primary-600 hover:underline">
          Вернуться к заказам
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <Link to="/orders" className="text-sm text-primary-600 hover:underline mb-4 inline-block">
        &larr; Все заказы
      </Link>

      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">Заказ #{order.id.slice(0, 8)}</h1>
        <span className={`px-3 py-1 rounded-full text-sm font-medium ${statusColors[order.status] ?? ''}`}>
          {statusLabels[order.status] ?? order.status}
        </span>
      </div>

      {order.createdAt && (
        <p className="text-sm text-gray-500 dark:text-gray-400 mb-2">
          Дата создания: {new Date(order.createdAt).toLocaleString('ru-RU')}
        </p>
      )}

      {order.deliveryAddress && (
        <p className="text-sm text-gray-500 dark:text-gray-400 mb-6">
          Адрес доставки: <span className="text-gray-700 dark:text-gray-300">{order.deliveryAddress}</span>
        </p>
      )}

      <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden">
        <table className="w-full">
          <thead>
            <tr className="border-b border-gray-200 dark:border-gray-700">
              <th className="text-left p-4 text-sm font-medium text-gray-500 dark:text-gray-400">Товар</th>
              <th className="text-center p-4 text-sm font-medium text-gray-500 dark:text-gray-400">Кол-во</th>
              <th className="text-right p-4 text-sm font-medium text-gray-500 dark:text-gray-400">Цена</th>
              <th className="text-right p-4 text-sm font-medium text-gray-500 dark:text-gray-400">Сумма</th>
            </tr>
          </thead>
          <tbody>
            {order.items.map((item) => (
              <tr key={item.id} className="border-b border-gray-100 dark:border-gray-700 last:border-0">
                <td className="p-4">
                  <Link
                    to={`/products/${item.productId}`}
                    className="hover:text-primary-600 transition-colors"
                  >
                    {item.name}
                  </Link>
                </td>
                <td className="p-4 text-center">{item.quantity}</td>
                <td className="p-4 text-right text-gray-500 dark:text-gray-400">
                  {item.priceAtPurchase.toLocaleString('ru-RU')} &#8381;
                </td>
                <td className="p-4 text-right font-medium">
                  {item.totalPriceAtPurchase.toLocaleString('ru-RU')} &#8381;
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="border-t border-gray-200 dark:border-gray-700 p-4 flex justify-between items-center">
          <span className="text-lg font-semibold">Итого:</span>
          <span className="text-2xl font-bold text-primary-600">
            {order.totalOrderPrice.toLocaleString('ru-RU')} &#8381;
          </span>
        </div>
      </div>
    </div>
  );
}
