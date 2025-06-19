import { ValueObject } from './value-object';
import { InvalidQuantityError } from '../errors/invalid-quantity.error';

export class Quantity extends ValueObject<number> {
  constructor(value: number) {
    if (value < 0) {
      throw new InvalidQuantityError(value);
    }
    super(value);
  }
}
