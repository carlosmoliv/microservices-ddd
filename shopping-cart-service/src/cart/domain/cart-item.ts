import { CartItemId } from './value-objects/cart-item-id';
import { ProductId } from './value-objects/product-id';
import { Money } from './value-objects/money';
import { Quantity } from './value-objects/quantity';

export class CartItem {
  private constructor(
    private readonly _id: CartItemId,
    private readonly _productId: ProductId,
    private _productName: string,
    private _price: Money,
    private _quantity: Quantity,
    private _addedAt: Date,
  ) {}

  static create(
    productId: ProductId,
    productName: string,
    price: Money,
    quantity: Quantity,
  ): CartItem {
    return new CartItem(
      CartItemId.generate(),
      productId,
      productName,
      price,
      quantity,
      new Date(),
    );
  }

  updateQuantity(quantity: Quantity): void {
    this._quantity = quantity;
  }

  updatePrice(price: Money): void {
    this._price = price;
  }

  subtotal(): Money {
    return this._price.multiply(this._quantity.value);
  }

  get id(): CartItemId {
    return this._id;
  }

  get productId(): ProductId {
    return this._productId;
  }

  get productName(): string {
    return this._productName;
  }

  get price(): Money {
    return this._price;
  }

  get quantity(): Quantity {
    return this._quantity;
  }

  get addedAt(): Date {
    return this._addedAt;
  }
}
