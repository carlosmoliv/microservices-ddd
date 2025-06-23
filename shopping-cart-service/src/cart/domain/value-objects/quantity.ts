import { InvalidQuantityError } from '../errors/invalid-quantity.error';

export class Quantity {
  constructor(readonly value: number) {
    if (value < 0) {
      throw new InvalidQuantityError(value);
    }
  }

  equals(other: Quantity): boolean {
    return this.value === other.value;
  }

  toJson(): { value: number } {
    return { value: this.value };
  }
}
