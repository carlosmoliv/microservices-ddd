import { DomainError } from './domain-error';

export class ItemNotFoundError extends DomainError {
  readonly code = 'ITEM_NOT_FOUND';
  readonly statusCode = 404;

  constructor(itemId: string, itemType: string = 'Item') {
    super(`${itemType} with ID '${itemId}' was not found`);
  }
}
