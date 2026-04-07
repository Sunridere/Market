import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';

export function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center py-20 px-4">
      <h1 className="text-6xl font-bold text-primary-600 mb-4">404</h1>
      <p className="text-xl text-gray-500 dark:text-gray-400 mb-8">Страница не найдена</p>
      <Link to="/">
        <Button>На главную</Button>
      </Link>
    </div>
  );
}
