import { randomUUID } from 'crypto';
import { ValueObject } from './value-object';

export class CartId extends ValueObject<string> {
  static generate(): CartId {
    return new CartId(randomUUID());
  }
}
