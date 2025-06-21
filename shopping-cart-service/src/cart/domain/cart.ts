import { CartId } from './value-objects/cart-id';
import { UserId } from './value-objects/user-id';
import { CartItem } from './cart-item';
import { ProductId } from './value-objects/product-id';
import { Money } from './value-objects/money';
import { Quantity } from './value-objects/quantity';
import { ItemAddedToCartEvent } from './events/item-added-to-cart.event';
import { AggregateRoot } from './aggregate-root';

export class Cart extends AggregateRoot {
  private constructor(
    private readonly _id: CartId,
    private readonly _userId: UserId,
    private _items: CartItem[],
    private _createdAt: Date,
    private _updatedAt: Date,
  ) {
    super();
  }

  static create(userId: UserId): Cart {
    return new Cart(CartId.generate(), userId, [], new Date(), new Date());
  }

  addItem(
    productId: ProductId,
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
      this._items.push(newItem);
    }

    this._updatedAt = new Date();

    this.addDomainEvent(
      new ItemAddedToCartEvent(this._id, productId, quantity),
    );
  }

  get id(): CartId {
    return this._id;
  }

  get userId(): UserId {
    return this._userId;
  }

  get items(): readonly CartItem[] {
    return [...this._items];
  }

  get itemCount(): number {
    return this._items.length;
  }

  get isEmpty(): boolean {
    return this._items.length === 0;
  }

  private ensureCartIsActive(): void {
    // Business rule: can't modify abandoned carts, etc.
  }

  private findItem(productId: ProductId): CartItem | undefined {
    return this._items.find((item) => item.productId.equals(productId));
  }
}
