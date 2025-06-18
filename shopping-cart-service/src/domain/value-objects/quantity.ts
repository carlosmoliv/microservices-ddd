import { ValueObject } from './value-object';

class InvalidQuantityError extends Error {}

export class Quantity extends ValueObject<number> {
  constructor(value: number) {
    if (value <= 0) {
      throw new InvalidQuantityError();
    }
    super(value);
  }
}
