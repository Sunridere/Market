import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { productService } from '../services/product.service';
import { ProductCard } from '../components/product/ProductCard';
import { Spinner } from '../components/ui/Spinner';
import { Button } from '../components/ui/Button';
import type { ProductDto } from '../types/product';

const PAGE_SIZE = 8;

export function HomePage() {
  const [products, setProducts] = useState<ProductDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    setLoading(true);
    productService
      .getAll(currentPage - 1, PAGE_SIZE)
      .then(({ data }) => {
        setProducts(data.content);
        setTotalPages(data.totalPages);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [currentPage]);

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-r from-primary-600 to-primary-800 text-white">
        <div className="max-w-7xl mx-auto px-4 py-20">
          <h1 className="text-4xl font-bold mb-4">TechMarket</h1>
          <p className="text-xl text-primary-100 mb-8 max-w-xl">
            Лучшая техника по выгодным ценам. Ноутбуки, смартфоны, аксессуары и многое другое.
          </p>
          <Link to="/catalog">
            <Button size="lg" variant="secondary">
              Перейти в каталог
            </Button>
          </Link>
        </div>
      </section>

      {/* Featured products */}
      <section className="max-w-7xl mx-auto px-4 py-12">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-2xl font-bold">Популярные товары</h2>
          <Link to="/catalog" className="text-primary-600 hover:underline text-sm font-medium">
            Смотреть все &rarr;
          </Link>
        </div>

        {loading ? (
          <div className="py-12">
            <Spinner size="lg" />
          </div>
        ) : (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {products.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>

            {totalPages > 1 && (
              <div className="flex justify-center items-center gap-2 mt-8">
                <button
                  onClick={() => setCurrentPage((p) => p - 1)}
                  disabled={currentPage === 1}
                  className="px-3 py-2 rounded-lg border border-gray-300 dark:border-gray-600
                    disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                >
                  &larr;
                </button>
                {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                  <button
                    key={page}
                    onClick={() => setCurrentPage(page)}
                    className={`px-3 py-2 rounded-lg border transition-colors ${
                      page === currentPage
                        ? 'bg-primary-600 text-white border-primary-600'
                        : 'border-gray-300 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-700'
                    }`}
                  >
                    {page}
                  </button>
                ))}
                <button
                  onClick={() => setCurrentPage((p) => p + 1)}
                  disabled={currentPage === totalPages}
                  className="px-3 py-2 rounded-lg border border-gray-300 dark:border-gray-600
                    disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                >
                  &rarr;
                </button>
              </div>
            )}
          </>
        )}
      </section>
    </div>
  );
}
