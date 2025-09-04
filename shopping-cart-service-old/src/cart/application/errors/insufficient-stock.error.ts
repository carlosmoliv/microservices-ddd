import { DomainError } from '../../domain/errors/domain-error';

export class InsufficientStockError extends DomainError {
  readonly code = 'INSUFFICIENT_STOCK';

  constructor(
    message: string = 'Insufficient stock for the requested quantity.',
  ) {
    super(message);
  }
}
