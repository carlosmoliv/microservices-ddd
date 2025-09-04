interface ProductDetails {
  id: string;
  name: string;
  priceAmount: number;
  stockQuantity: number;
  version: number;
}

interface ProductDetailsWithStockResponse {
  product: ProductDetails;
  hasStock: boolean;
  availableQuantity: number;
}
