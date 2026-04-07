import { useCart } from '../../hooks/useCart';
import type { CartItemDto } from '../../types/cart';

interface CartItemProps {
  item: CartItemDto;
}

export function CartItem({ item }: CartItemProps) {
  const { updateQuantity, removeItem } = useCart();

  return (
    <div className="flex items-center gap-4 p-4 bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700">
      <div className="flex-1 min-w-0">
        <h3 className="font-medium truncate">{item.name}</h3>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          {item.unitPrice.toLocaleString('ru-RU')} &#8381; / шт.
        </p>
      </div>

      <div className="flex items-center gap-2">
        <button
          onClick={() => updateQuantity(item.productId, item.quantity - 1)}
          disabled={item.quantity <= 1}
          className="w-8 h-8 flex items-center justify-center rounded-lg border border-gray-300 dark:border-gray-600
            hover:bg-gray-100 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          -
        </button>
        <span className="w-10 text-center font-medium">{item.quantity}</span>
        <button
          onClick={() => updateQuantity(item.productId, item.quantity + 1)}
          className="w-8 h-8 flex items-center justify-center rounded-lg border border-gray-300 dark:border-gray-600
            hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
        >
          +
        </button>
      </div>

      <p className="w-28 text-right font-bold">
        {item.totalPrice.toLocaleString('ru-RU')} &#8381;
      </p>

      <button
        onClick={() => removeItem(item.productId)}
        className="p-2 text-gray-400 hover:text-red-500 transition-colors"
        title="Удалить"
      >
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
        </svg>
      </button>
    </div>
  );
}
