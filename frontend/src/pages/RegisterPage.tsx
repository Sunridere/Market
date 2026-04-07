import { useState, type FormEvent } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { authService } from '../services/auth.service';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import toast from 'react-hot-toast';

export function RegisterPage() {
  const { isAuthenticated, login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
  });
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) return <Navigate to="/" replace />;

  const handleChange = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { data } = await authService.signUp(form);
      await login(data.accessToken, data.refreshToken);
      toast.success('Регистрация успешна!');
      navigate('/');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { errorMessage?: string } } })
        ?.response?.data?.errorMessage ?? 'Ошибка регистрации';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-20 px-4">
      <h1 className="text-2xl font-bold text-center mb-8">Регистрация</h1>
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
          label="Пароль"
          type="password"
          value={form.password}
          onChange={handleChange('password')}
          required
          minLength={8}
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
          {loading ? 'Регистрация...' : 'Зарегистрироваться'}
        </Button>
      </form>
      <p className="text-center mt-4 text-sm text-gray-500 dark:text-gray-400">
        Уже есть аккаунт?{' '}
        <Link to="/login" className="text-primary-600 hover:underline">
          Войти
        </Link>
      </p>
    </div>
  );
}
