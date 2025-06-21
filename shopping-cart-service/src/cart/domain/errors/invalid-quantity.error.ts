import { DomainError } from './domain-error';

export class InvalidQuantityError extends DomainError {
  readonly code = 'INVALID_QUANTITY';
  readonly statusCode = 400;

  constructor(
    providedValue: number,
    reason: string = 'Quantity must be greater than zero',
  ) {
    super(`Invalid quantity: ${providedValue}. ${reason}`);
  }
}
