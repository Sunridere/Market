import { useState, type FormEvent } from 'react';
import { useAuth } from '../hooks/useAuth';
import { userService } from '../services/user.service';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import toast from 'react-hot-toast';

export function ProfilePage() {
  const { user, updateUser } = useAuth();
  const [form, setForm] = useState({
    username: user?.username ?? '',
    email: user?.email ?? '',
    firstName: user?.firstName ?? '',
    lastName: user?.lastName ?? '',
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!user) return;
    setLoading(true);
    try {
      const { data } = await userService.updateMe({
        id: user.id,
        username: form.username,
        email: form.email,
        firstName: form.firstName || null,
        lastName: form.lastName || null,
      });
      updateUser(data);
      toast.success('Профиль обновлён');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { errorMessage?: string } } })
        ?.response?.data?.errorMessage ?? 'Ошибка при обновлении профиля';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-lg mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Профиль</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Имя пользователя"
          value={form.username}
          onChange={handleChange('username')}
          required
          minLength={5}
          maxLength={50}
        />
        <Input
          label="Email"
          type="email"
          value={form.email}
          onChange={handleChange('email')}
          required
        />
        <Input
          label="Имя"
          value={form.firstName}
          onChange={handleChange('firstName')}
        />
        <Input
          label="Фамилия"
          value={form.lastName}
          onChange={handleChange('lastName')}
        />
        <Button type="submit" disabled={loading} className="w-full">
          {loading ? 'Сохранение...' : 'Сохранить'}
        </Button>
      </form>
    </div>
  );
}
