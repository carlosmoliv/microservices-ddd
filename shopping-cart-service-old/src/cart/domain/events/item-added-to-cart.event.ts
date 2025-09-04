import { Quantity } from '../value-objects/quantity';

export class ItemAddedToCartEvent {
  constructor(
    public readonly cartId: string,
    public readonly productId: string,
    public readonly quantity: Quantity,
  ) {}
}
