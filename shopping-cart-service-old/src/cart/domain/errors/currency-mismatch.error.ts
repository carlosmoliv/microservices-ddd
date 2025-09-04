import { DomainError } from './domain-error';

export class CurrencyMismatchError extends DomainError {
  readonly code: string = 'CURRENCY_MISMATCH';
  readonly statusCode: number = 400;

  constructor(fromCurrency: string, toCurrency: string, cause?: Error) {
    const message = `Currency mismatch: cannot operate on ${fromCurrency} and ${toCurrency}.`;
    super(message, cause);
  }
}
