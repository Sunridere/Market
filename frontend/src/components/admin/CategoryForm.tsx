import { useState, type FormEvent } from 'react';
import { categoryService } from '../../services/category.service';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import toast from 'react-hot-toast';

interface CategoryFormProps {
  onSaved: () => void;
}

export function CategoryForm({ onSaved }: CategoryFormProps) {
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await categoryService.create({ name });
      toast.success('Категория создана');
      setName('');
      onSaved();
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { errorMessage?: string } } })
        ?.response?.data?.errorMessage ?? 'Ошибка при создании категории';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex gap-3">
      <Input
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Название категории"
        required
        className="flex-1"
      />
      <Button type="submit" disabled={loading || !name.trim()}>
        {loading ? '...' : 'Добавить'}
      </Button>
    </form>
  );
}
