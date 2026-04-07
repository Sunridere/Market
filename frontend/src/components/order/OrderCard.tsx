import { Link } from 'react-router-dom';
import type { OrderDto } from '../../types/order';

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

interface OrderCardProps {
  order: OrderDto;
}

export function OrderCard({ order }: OrderCardProps) {
  return (
    <Link
      to={`/orders/${order.id}`}
      className="block bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700
        p-4 hover:shadow-md transition-shadow"
    >
      <div className="flex items-center justify-between mb-3">
        <div>
          <span className="text-sm text-gray-500 dark:text-gray-400">
            Заказ #{order.id.slice(0, 8)}
          </span>
          {order.createdAt && (
            <span className="text-xs text-gray-400 dark:text-gray-500 ml-2">
              {new Date(order.createdAt).toLocaleDateString('ru-RU')}
            </span>
          )}
        </div>
        <span className={`px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[order.status] ?? ''}`}>
          {statusLabels[order.status] ?? order.status}
        </span>
      </div>
      <div className="flex items-center justify-between">
        <span className="text-sm text-gray-500 dark:text-gray-400">
          {order.items.length} {order.items.length === 1 ? 'товар' : 'товаров'}
        </span>
        <span className="font-bold text-lg">
          {order.totalOrderPrice.toLocaleString('ru-RU')} &#8381;
        </span>
      </div>
    </Link>
  );
}
