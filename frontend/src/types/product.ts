export interface CategoryDto {
  id: string;
  name: string;
}

export interface ProductImageDto {
  id: string;
  imageUrl: string;
  isMain: boolean;
}

export interface ProductDto {
  id: string;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  category: CategoryDto;
  images: ProductImageDto[];
  createdAt?: string;
  updatedAt?: string;
}
