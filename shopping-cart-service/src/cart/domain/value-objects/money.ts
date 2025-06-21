import { ValueObject } from './value-object';
import { CurrencyMismatchError } from '../errors/currency-mismatch.error';

export class Money extends ValueObject<{ amount: number; currency: string }> {
  static zero(): Money {
    return new Money({ amount: 0, currency: 'USD' });
  }

  add(other: Money): Money {
    this.ensureSameCurrency(other);
    return new Money({
      amount: this.value.amount + other.value.amount,
      currency: this.value.currency,
    });
  }

  multiply(factor: number): Money {
    return new Money({
      amount: this.value.amount * factor,
      currency: this.value.currency,
    });
  }

  private ensureSameCurrency(other: Money): void {
    if (this.value.currency !== other.value.currency) {
      throw new CurrencyMismatchError(
        this.value.currency,
        other.value.currency,
      );
    }
  }
}
