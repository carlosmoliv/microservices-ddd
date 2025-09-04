import { Money } from './money';
import { CurrencyMismatchError } from '../errors/currency-mismatch.error';

describe('Money Value Object', () => {
  it('should create Money with correct amount and currency', () => {
    const money = new Money({ amount: 100, currency: 'USD' });

    expect(money.value.amount).toBe(100);
    expect(money.value.currency).toBe('USD');
  });

  it('should be equal if amount and currency are the same', () => {
    const m1 = new Money({ amount: 50, currency: 'USD' });
    const m2 = new Money({ amount: 50, currency: 'USD' });

    expect(m1.equals(m2)).toBe(true);
  });

  it('should not be equal if amounts differ', () => {
    const m1 = new Money({ amount: 50, currency: 'USD' });
    const m2 = new Money({ amount: 60, currency: 'USD' });

    expect(m1.equals(m2)).toBe(false);
  });

  it('should not be equal if currencies differ', () => {
    const m1 = new Money({ amount: 50, currency: 'USD' });
    const m2 = new Money({ amount: 50, currency: 'BRL' });

    expect(m1.equals(m2)).toBe(false);
  });

  describe('add', () => {
    it('should add two money objects of the same currency', () => {
      const m1 = new Money({ amount: 10, currency: 'USD' });
      const m2 = new Money({ amount: 20, currency: 'USD' });

      const result = m1.add(m2);

      expect(result.value.amount).toBe(30);
      expect(result.value.currency).toBe('USD');
    });

    it('should throw error when adding different currencies', () => {
      const m1 = new Money({ amount: 10, currency: 'USD' });
      const m2 = new Money({ amount: 20, currency: 'BRL' });

      expect(() => m1.add(m2)).toThrow(CurrencyMismatchError);
    });
  });

  describe('multiply', () => {
    it('should multiply money by a number', () => {
      const money = new Money({ amount: 10.5, currency: 'USD' });

      const result = money.multiply(4);

      expect(result.value.amount).toBe(42.0);
      expect(result.value.currency).toBe('USD');
    });

    it('should handle decimal multiplication', () => {
      const money = new Money({ amount: 12.33, currency: 'USD' });

      const result = money.multiply(2.5);

      expect(result.value.amount).toBe(30.825);
      expect(result.value.currency).toBe('USD');
    });
  });
});
