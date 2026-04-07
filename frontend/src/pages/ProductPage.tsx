import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { productService } from '../services/product.service';
import { useAuth } from '../hooks/useAuth';
import { useCart } from '../hooks/useCart';
import { ImageCarousel } from '../components/product/ImageCarousel';
import { Button } from '../components/ui/Button';
import { Spinner } from '../components/ui/Spinner';
import type { ProductDto } from '../types/product';

export function ProductPage() {
  const { id } = useParams<{ id: string }>();
  const { isAuthenticated } = useAuth();
  const { addToCart } = useCart();
  const [product, setProduct] = useState<ProductDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    productService
      .getById(id)
      .then(({ data }) => setProduct(data))
      .catch(() => setProduct(null))
      .finally(() => setLoading(false));
  }, [id]);

  const handleAddToCart = async () => {
    if (!product) return;
    setAdding(true);
    await addToCart(product.id, 1);
    setAdding(false);
  };

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!product) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 text-center">
        <h1 className="text-2xl font-bold mb-4">Товар не найден</h1>
        <Link to="/catalog" className="text-primary-600 hover:underline">
          Вернуться в каталог
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Breadcrumb */}
      <nav className="text-sm text-gray-500 dark:text-gray-400 mb-6">
        <Link to="/catalog" className="hover:text-primary-600">Каталог</Link>
        <span className="mx-2">/</span>
        <span>{product.category.name}</span>
        <span className="mx-2">/</span>
        <span className="text-gray-900 dark:text-gray-100">{product.name}</span>
      </nav>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
        {/* Images */}
        <ImageCarousel images={product.images} />

        {/* Info */}
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-2">
            {product.category.name}
          </p>
          <h1 className="text-3xl font-bold mb-4">{product.name}</h1>
          <p className="text-3xl font-bold text-primary-600 mb-6">
            {product.price.toLocaleString('ru-RU')} &#8381;
          </p>

          <div className="flex items-center gap-4 mb-8">
            {product.stockQuantity > 0 ? (
              <span className="inline-flex items-center gap-1 text-sm text-green-600">
                <span className="w-2 h-2 bg-green-500 rounded-full" />
                В наличии ({product.stockQuantity} шт.)
              </span>
            ) : (
              <span className="inline-flex items-center gap-1 text-sm text-red-500">
                <span className="w-2 h-2 bg-red-500 rounded-full" />
                Нет в наличии
              </span>
            )}
          </div>

          {isAuthenticated ? (
            <Button
              size="lg"
              onClick={handleAddToCart}
              disabled={adding || product.stockQuantity <= 0}
              className="w-full lg:w-auto"
            >
              {adding ? 'Добавление...' : 'Добавить в корзину'}
            </Button>
          ) : (
            <Link to="/login">
              <Button size="lg" variant="secondary" className="w-full lg:w-auto">
                Войдите, чтобы купить
              </Button>
            </Link>
          )}

          <div className="mt-8 border-t border-gray-200 dark:border-gray-700 pt-8">
            <h2 className="text-lg font-semibold mb-4">Описание</h2>
            <p className="text-gray-600 dark:text-gray-400 whitespace-pre-line leading-relaxed">
              {product.description}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
