import { Link } from 'react-router-dom';

const links = [
  {
    to: '/admin/products',
    title: 'Товары',
    description: 'Создание, редактирование и удаление товаров',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
          d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
      </svg>
    ),
  },
  {
    to: '/admin/categories',
    title: 'Категории',
    description: 'Управление категориями товаров',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
          d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
      </svg>
    ),
  },
  {
    to: '/admin/users',
    title: 'Пользователи',
    description: 'Просмотр и блокировка пользователей',
    icon: (
      <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
          d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
      </svg>
    ),
  },
];

export function AdminDashboard() {
  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-8">Админ-панель</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {links.map((link) => (
          <Link
            key={link.to}
            to={link.to}
            className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700
              p-6 hover:shadow-lg transition-shadow group"
          >
            <div className="text-primary-600 mb-4 group-hover:scale-110 transition-transform inline-block">
              {link.icon}
            </div>
            <h2 className="text-lg font-semibold mb-1">{link.title}</h2>
            <p className="text-sm text-gray-500 dark:text-gray-400">{link.description}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
