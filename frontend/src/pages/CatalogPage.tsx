import { useEffect, useState, useMemo } from 'react';
import { productService } from '../services/product.service';
import { categoryService } from '../services/category.service';
import { ProductGrid } from '../components/product/ProductGrid';
import { Spinner } from '../components/ui/Spinner';
import type { ProductDto, CategoryDto } from '../types/product';

const ITEMS_PER_PAGE = 12;

export function CatalogPage() {
  const [allProducts, setAllProducts] = useState<ProductDto[]>([]);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [loading, setLoading] = useState(true);

  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [sortBy, setSortBy] = useState<'price-asc' | 'price-desc' | 'name'>('name');
  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    Promise.all([productService.getAll(0, 1000), categoryService.getAll()])
      .then(([productsRes, categoriesRes]) => {
        setAllProducts(productsRes.data.content);
        setCategories(categoriesRes.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const filtered = useMemo(() => {
    let result = [...allProducts];

    if (selectedCategory) {
      result = result.filter((p) => p.category.id === selectedCategory);
    }

    const min = minPrice ? parseFloat(minPrice) : 0;
    const max = maxPrice ? parseFloat(maxPrice) : Infinity;
    result = result.filter((p) => p.price >= min && p.price <= max);

    result.sort((a, b) => {
      if (sortBy === 'price-asc') return a.price - b.price;
      if (sortBy === 'price-desc') return b.price - a.price;
      return a.name.localeCompare(b.name, 'ru');
    });

    return result;
  }, [allProducts, selectedCategory, minPrice, maxPrice, sortBy]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / ITEMS_PER_PAGE));
  const paginated = filtered.slice(
    (currentPage - 1) * ITEMS_PER_PAGE,
    currentPage * ITEMS_PER_PAGE
  );

  useEffect(() => {
    setCurrentPage(1);
  }, [selectedCategory, minPrice, maxPrice, sortBy]);

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Каталог</h1>

      <div className="flex gap-8">
        {/* Sidebar filters */}
        <aside className="w-64 flex-shrink-0">
          <div className="sticky top-20 space-y-6">
            {/* Category filter */}
            <div>
              <h3 className="text-sm font-semibold mb-3 uppercase tracking-wide text-gray-500 dark:text-gray-400">
                Категория
              </h3>
              <div className="space-y-2">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="category"
                    checked={selectedCategory === ''}
                    onChange={() => setSelectedCategory('')}
                    className="text-primary-600"
                  />
                  <span className="text-sm">Все категории</span>
                </label>
                {categories.map((cat) => (
                  <label key={cat.id} className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      name="category"
                      checked={selectedCategory === cat.id}
                      onChange={() => setSelectedCategory(cat.id)}
                      className="text-primary-600"
                    />
                    <span className="text-sm">{cat.name}</span>
                  </label>
                ))}
              </div>
            </div>

            {/* Price filter */}
            <div>
              <h3 className="text-sm font-semibold mb-3 uppercase tracking-wide text-gray-500 dark:text-gray-400">
                Цена
              </h3>
              <div className="flex gap-2">
                <input
                  type="number"
                  placeholder="От"
                  value={minPrice}
                  onChange={(e) => setMinPrice(e.target.value)}
                  className="w-full px-3 py-2 text-sm border rounded-lg bg-white dark:bg-gray-800
                    border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
                <input
                  type="number"
                  placeholder="До"
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(e.target.value)}
                  className="w-full px-3 py-2 text-sm border rounded-lg bg-white dark:bg-gray-800
                    border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>
            </div>

            {/* Sort */}
            <div>
              <h3 className="text-sm font-semibold mb-3 uppercase tracking-wide text-gray-500 dark:text-gray-400">
                Сортировка
              </h3>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value as typeof sortBy)}
                className="w-full px-3 py-2 text-sm border rounded-lg bg-white dark:bg-gray-800
                  border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="name">По названию</option>
                <option value="price-asc">Сначала дешёвые</option>
                <option value="price-desc">Сначала дорогие</option>
              </select>
            </div>

            <p className="text-xs text-gray-400 dark:text-gray-500">
              Найдено: {filtered.length} товаров
            </p>
          </div>
        </aside>

        {/* Product grid */}
        <div className="flex-1">
          <ProductGrid
            products={paginated}
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        </div>
      </div>
    </div>
  );
}
