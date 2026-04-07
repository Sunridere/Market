import { Link } from 'react-router-dom';
import type { ProductDto } from '../../types/product';

interface ProductCardProps {
  product: ProductDto;
}

export function ProductCard({ product }: ProductCardProps) {
  const mainImage = product.images.find((img) => img.isMain) ?? product.images[0];

  return (
    <Link
      to={`/products/${product.id}`}
      className="group bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700
        overflow-hidden hover:shadow-lg transition-shadow"
    >
      <div className="aspect-square bg-gray-100 dark:bg-gray-700 overflow-hidden">
        {mainImage ? (
          <img
            src={mainImage.imageUrl}
            alt={product.name}
            className="w-full h-full object-contain group-hover:scale-105 transition-transform duration-300"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-gray-400">
            <svg className="w-16 h-16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1}
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
        )}
      </div>
      <div className="p-4">
        <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">
          {product.category.name}
        </p>
        <h3 className="font-medium text-sm mb-2 line-clamp-2 group-hover:text-primary-600 transition-colors">
          {product.name}
        </h3>
        <p className="text-lg font-bold text-primary-600">
          {product.price.toLocaleString('ru-RU')} &#8381;
        </p>
        {product.stockQuantity <= 0 && (
          <p className="text-xs text-red-500 mt-1">Нет в наличии</p>
        )}
      </div>
    </Link>
  );
}
