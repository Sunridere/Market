import { userService } from '../../services/user.service';
import { Button } from '../ui/Button';
import type { UserDto } from '../../types/user';
import toast from 'react-hot-toast';

interface UserTableProps {
  users: UserDto[];
  onUpdated: () => void;
}

export function UserTable({ users, onUpdated }: UserTableProps) {
  const handleBlock = async (userId: string) => {
    try {
      await userService.blockUser(userId);
      toast.success('Пользователь заблокирован');
      onUpdated();
    } catch {
      toast.error('Ошибка при блокировке');
    }
  };

  if (users.length === 0) {
    return <p className="text-gray-500 dark:text-gray-400 text-center py-8">Нет пользователей</p>;
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead>
          <tr className="border-b border-gray-200 dark:border-gray-700">
            <th className="text-left p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Имя пользователя</th>
            <th className="text-left p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Email</th>
            <th className="text-left p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Имя</th>
            <th className="text-right p-3 text-sm font-medium text-gray-500 dark:text-gray-400">Действия</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id} className="border-b border-gray-100 dark:border-gray-700 last:border-0">
              <td className="p-3 font-medium">{user.username}</td>
              <td className="p-3 text-gray-500 dark:text-gray-400">{user.email}</td>
              <td className="p-3">{[user.firstName, user.lastName].filter(Boolean).join(' ') || '—'}</td>
              <td className="p-3 text-right">
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => handleBlock(user.id)}
                >
                  Заблокировать
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
