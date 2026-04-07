import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { categoryService } from '../../services/category.service';
import { CategoryForm } from '../../components/admin/CategoryForm';
import { Spinner } from '../../components/ui/Spinner';
import type { CategoryDto } from '../../types/product';

export function AdminCategories() {
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    categoryService
      .getAll()
      .then(({ data }) => setCategories(data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <Link to="/admin" className="text-sm text-primary-600 hover:underline mb-4 inline-block">
        &larr; Админ-панель
      </Link>

      <h1 className="text-2xl font-bold mb-6">Категории ({categories.length})</h1>

      <div className="mb-6">
        <CategoryForm onSaved={load} />
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700">
        {categories.length === 0 ? (
          <p className="text-center text-gray-500 dark:text-gray-400 py-8">Нет категорий</p>
        ) : (
          <ul className="divide-y divide-gray-200 dark:divide-gray-700">
            {categories.map((cat) => (
              <li key={cat.id} className="px-4 py-3 flex items-center justify-between">
                <span className="font-medium">{cat.name}</span>
                <span className="text-xs text-gray-400">{cat.id.slice(0, 8)}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
