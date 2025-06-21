import { CartId } from '../value-objects/cart-id';
import { ProductId } from '../value-objects/product-id';
import { Quantity } from '../value-objects/quantity';
import { DomainEvent } from './domain-event';

export class ItemAddedToCartEvent extends DomainEvent {
  constructor(
    public readonly cartId: CartId,
    public readonly productId: ProductId,
    public readonly quantity: Quantity,
  ) {
    super();
  }
}
