import { CurrencyMismatchError } from '../errors/currency-mismatch.error';

export class Money {
  constructor(readonly value: { amount: number; currency: 'USD' | 'BRL' }) {}

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

  equals(other: Money): boolean {
    return (
      this.value.amount === other.value.amount &&
      this.value.currency === other.value.currency
    );
  }

  toJson(): { amount: number; currency: string } {
    return {
      amount: this.value.amount,
      currency: this.value.currency,
    };
  }
}
