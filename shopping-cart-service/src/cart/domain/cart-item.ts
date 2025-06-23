import { Money } from './value-objects/money';
import { Quantity } from './value-objects/quantity';
import { randomUUID } from 'crypto';

export class CartItem {
  private constructor(
    public id: string,
    public productId: string,
    public productName: string,
    public price: Money,
    public quantity: Quantity,
  ) {}

  static create(
    productId: string,
    productName: string,
    price: Money,
    quantity: Quantity,
  ): CartItem {
    return new CartItem(randomUUID(), productId, productName, price, quantity);
  }

  updateQuantity(quantity: Quantity): void {
    this.quantity = quantity;
  }

  updatePrice(price: Money): void {
    this.price = price;
  }

  subtotal(): Money {
    return this.price.multiply(this.quantity.value);
  }
}
