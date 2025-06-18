import { ValueObject } from './value-object';
import { randomUUID } from 'crypto';

export class CartItemId extends ValueObject<string> {
  static generate(): CartItemId {
    return new CartItemId(randomUUID());
  }
}
