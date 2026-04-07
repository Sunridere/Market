import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { productService } from '../../services/product.service';
import { categoryService } from '../../services/category.service';
import { ProductForm } from '../../components/admin/ProductForm';
import { Button } from '../../components/ui/Button';
import { Modal } from '../../components/ui/Modal';
import { Spinner } from '../../components/ui/Spinner';
import type { ProductDto, CategoryDto } from '../../types/product';
import toast from 'react-hot-toast';

const PAGE_SIZE = 20;

export function AdminProducts() {
  const [products, setProducts] = useState<ProductDto[]>([]);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editProduct, setEditProduct] = useState<ProductDto | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const load = (page = currentPage) => {
    setLoading(true);
    Promise.all([productService.getAll(page - 1, PAGE_SIZE), categoryService.getAll()])
      .then(([p, c]) => {
        setProducts(p.data.content);
        setTotalPages(p.data.totalPages);
        setTotalElements(p.data.totalElements);
        setCategories(c.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [currentPage]);

  const handleDelete = async (id: string) => {
    if (!confirm('Удалить товар?')) return;
    try {
      await productService.delete(id);
      toast.success('Товар удалён');
      load(currentPage);
    } catch {
      toast.error('Ошибка при удалении');
    }
  };

  const openCreate = () => {
    setEditProduct(null);
    setShowForm(true);
  };

  const openEdit = (product: ProductDto) => {
    setEditProduct(product);
    setShowForm(true);
  };

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <Link to="/admin" className="text-sm text-primary-600 hover:underline mb-4 inline-block">
        &larr; Админ-панель
      </Link>

      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Товары ({totalElements})</h1>
        <Button onClick={openCreate}>Добавить товар</Button>
      </div>

      <Modal
        isOpen={showForm}
        onClose={() => setShowForm(false)}
        title={editProduct ? 'Редактировать товар' : 'Новый товар'}
      >
        <ProductForm
          product={editProduct}
          categories={categories}
          onSaved={() => {
            setShowForm(false);
            load(currentPage);
          }}
          onCancel={() => setShowForm(false)}
        />
      </Modal>

      <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden">
        <table className="w-full">
          <thead>
            <tr className="border-b border-gray-200 dark:border-gray-700">
              <th className="text-left p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Товар</th>
              <th className="text-left p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Категория</th>
              <th className="text-right p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Цена</th>
              <th className="text-right p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Остаток</th>
              <th className="text-right p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Действия</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.id} className="border-b border-gray-100 dark:border-gray-700 last:border-0">
                <td className="p-3">
                  <div className="flex items-center gap-3">
                    {product.images[0] ? (
                      <img
                        src={product.images[0].imageUrl}
                        alt=""
                        className="w-10 h-10 rounded-lg object-cover"
                      />
                    ) : (
                      <div className="w-10 h-10 rounded-lg bg-gray-100 dark:bg-gray-700" />
                    )}
                    <span className="font-medium">{product.name}</span>
                  </div>
                </td>
                <td className="p-3 text-gray-500 dark:text-gray-400">{product.category.name}</td>
                <td className="p-3 text-right">{product.price.toLocaleString('ru-RU')} &#8381;</td>
                <td className="p-3 text-right">{product.stockQuantity}</td>
                <td className="p-3 text-right">
                  <div className="flex items-center justify-end gap-2">
                    <Button size="sm" variant="secondary" onClick={() => openEdit(product)}>
                      Изменить
                    </Button>
                    <Button size="sm" variant="danger" onClick={() => handleDelete(product.id)}>
                      Удалить
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-2 mt-6">
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
    </div>
  );
}
