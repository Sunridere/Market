import { useState, type FormEvent } from 'react';
import { productService } from '../../services/product.service';
import { fileService } from '../../services/file.service';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import type { ProductDto, CategoryDto } from '../../types/product';
import toast from 'react-hot-toast';

interface ProductFormProps {
  product?: ProductDto | null;
  categories: CategoryDto[];
  onSaved: () => void;
  onCancel: () => void;
}

export function ProductForm({ product, categories, onSaved, onCancel }: ProductFormProps) {
  const [form, setForm] = useState({
    name: product?.name ?? '',
    description: product?.description ?? '',
    price: product?.price?.toString() ?? '',
    stockQuantity: product?.stockQuantity?.toString() ?? '',
    categoryId: product?.category?.id ?? '',
  });
  const [imageFiles, setImageFiles] = useState<File[]>([]);
  const [loading, setLoading] = useState(false);

  const handleChange = (field: string) => (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Upload images first
      const uploadedImages = await Promise.all(
        imageFiles.map(async (file, index) => {
          const { data } = await fileService.upload(file);
          return { id: '', imageUrl: data.url, isMain: index === 0 && !product?.images?.length };
        })
      );

      const existingImages = product?.images ?? [];
      const allImages = [...existingImages, ...uploadedImages];

      const category = categories.find((c) => c.id === form.categoryId);
      if (!category) {
        toast.error('Выберите категорию');
        setLoading(false);
        return;
      }

      const payload: ProductDto = {
        id: product?.id ?? '',
        name: form.name,
        description: form.description,
        price: parseFloat(form.price),
        stockQuantity: parseInt(form.stockQuantity),
        category,
        images: allImages,
      };

      if (product?.id) {
        await productService.update(payload);
        toast.success('Товар обновлён');
      } else {
        await productService.create(payload);
        toast.success('Товар создан');
      }

      onSaved();
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { errorMessage?: string } } })
        ?.response?.data?.errorMessage ?? 'Ошибка при сохранении товара';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <Input
        label="Название"
        value={form.name}
        onChange={handleChange('name')}
        required
      />
      <div className="space-y-1">
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Описание
        </label>
        <textarea
          value={form.description}
          onChange={handleChange('description')}
          required
          rows={4}
          className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-800
            border-gray-300 dark:border-gray-600 text-gray-900 dark:text-gray-100
            focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent resize-y"
        />
      </div>
      <div className="grid grid-cols-2 gap-4">
        <Input
          label="Цена"
          type="number"
          step="0.01"
          min="0.01"
          value={form.price}
          onChange={handleChange('price')}
          required
        />
        <Input
          label="Количество"
          type="number"
          min="0"
          value={form.stockQuantity}
          onChange={handleChange('stockQuantity')}
          required
        />
      </div>
      <div className="space-y-1">
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Категория
        </label>
        <select
          value={form.categoryId}
          onChange={handleChange('categoryId')}
          required
          className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-800
            border-gray-300 dark:border-gray-600 text-gray-900 dark:text-gray-100
            focus:outline-none focus:ring-2 focus:ring-primary-500"
        >
          <option value="">Выберите категорию</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>
      </div>

      {/* Existing images */}
      {product?.images && product.images.length > 0 && (
        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Текущие изображения
          </label>
          <div className="flex gap-2 flex-wrap">
            {product.images.map((img) => (
              <img
                key={img.id}
                src={img.imageUrl}
                alt=""
                className="w-16 h-16 object-cover rounded-lg border border-gray-200 dark:border-gray-600"
              />
            ))}
          </div>
        </div>
      )}

      <div className="space-y-1">
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Добавить изображения
        </label>
        <input
          type="file"
          accept="image/*"
          multiple
          onChange={(e) => setImageFiles(Array.from(e.target.files ?? []))}
          className="w-full text-sm text-gray-500 dark:text-gray-400
            file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0
            file:text-sm file:font-medium file:bg-primary-50 file:text-primary-700
            dark:file:bg-primary-900 dark:file:text-primary-200
            hover:file:bg-primary-100"
        />
      </div>

      <div className="flex gap-3 pt-2">
        <Button type="submit" disabled={loading} className="flex-1">
          {loading ? 'Сохранение...' : product?.id ? 'Обновить' : 'Создать'}
        </Button>
        <Button type="button" variant="secondary" onClick={onCancel}>
          Отмена
        </Button>
      </div>
    </form>
  );
}
