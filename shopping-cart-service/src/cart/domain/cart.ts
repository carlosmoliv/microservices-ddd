import { randomUUID } from 'crypto';
import { AggregateRoot } from '@nestjs/cqrs';
import { CartItem } from './cart-item';
import { Money } from './value-objects/money';
import { Quantity } from './value-objects/quantity';
import { ItemAddedToCartEvent } from './events/item-added-to-cart.event';

export class Cart extends AggregateRoot {
  constructor(
    public id: string,
    public userId: string,
    public items: CartItem[],
  ) {
    super();
  }

  static create(userId: string): Cart {
    return new Cart(randomUUID(), userId, []);
  }

  addItem(
    productId: string,
    productName: string,
    price: Money,
    quantity: Quantity,
  ): void {
    this.ensureCartIsActive();

    const existingItem = this.findItem(productId);
    if (existingItem) {
      existingItem.updateQuantity(quantity);
    } else {
      const newItem = CartItem.create(productId, productName, price, quantity);
      this.items.push(newItem);
    }

    this.apply(new ItemAddedToCartEvent(this.id, productId, quantity));
  }

  private ensureCartIsActive(): void {
    // Business rule: can't modify abandoned carts, etc.
  }

  private findItem(productId: string): CartItem | undefined {
    return this.items.find((item: CartItem) => item.productId === productId);
  }
}
