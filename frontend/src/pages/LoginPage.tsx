import { useState, type FormEvent } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { authService } from '../services/auth.service';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import toast from 'react-hot-toast';

export function LoginPage() {
  const { isAuthenticated, login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) return <Navigate to="/" replace />;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { data } = await authService.signIn({ username, password });
      await login(data.accessToken, data.refreshToken);
      toast.success('Добро пожаловать!');
      navigate('/');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { errorMessage?: string } } })
        ?.response?.data?.errorMessage ?? 'Ошибка входа';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-20 px-4">
      <h1 className="text-2xl font-bold text-center mb-8">Вход</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Имя пользователя"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          minLength={5}
          maxLength={50}
        />
        <Input
          label="Пароль"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={8}
        />
        <Button type="submit" disabled={loading} className="w-full">
          {loading ? 'Вход...' : 'Войти'}
        </Button>
      </form>
      <p className="text-center mt-4 text-sm text-gray-500 dark:text-gray-400">
        Нет аккаунта?{' '}
        <Link to="/register" className="text-primary-600 hover:underline">
          Зарегистрироваться
        </Link>
      </p>
    </div>
  );
}
