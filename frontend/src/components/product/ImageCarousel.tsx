import { useState } from 'react';
import type { ProductImageDto } from '../../types/product';

interface ImageCarouselProps {
  images: ProductImageDto[];
}

export function ImageCarousel({ images }: ImageCarouselProps) {
  const sorted = [...images].sort((a, b) => (b.isMain ? 1 : 0) - (a.isMain ? 1 : 0));
  const [activeIndex, setActiveIndex] = useState(0);

  if (sorted.length === 0) {
    return (
      <div className="aspect-square bg-gray-100 dark:bg-gray-700 rounded-xl flex items-center justify-center text-gray-400">
        <svg className="w-24 h-24" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1}
            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </div>
    );
  }

  return (
    <div>
      <div className="aspect-square bg-gray-100 dark:bg-gray-700 rounded-xl overflow-hidden mb-4">
        <img
          src={sorted[activeIndex]?.imageUrl}
          alt={`Изображение ${activeIndex + 1}`}
          className="w-full h-full object-contain"
        />
      </div>
      {sorted.length > 1 && (
        <div className="flex gap-2 overflow-x-auto pb-2">
          {sorted.map((image, index) => (
            <button
              key={image.id}
              onClick={() => setActiveIndex(index)}
              className={`flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 transition-colors ${
                index === activeIndex
                  ? 'border-primary-600'
                  : 'border-transparent hover:border-gray-300 dark:hover:border-gray-500'
              }`}
            >
              <img
                src={image.imageUrl}
                alt={`Миниатюра ${index + 1}`}
                className="w-full h-full object-contain"
              />
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
