export abstract class DomainError extends Error {
  abstract readonly code: string;

  constructor(message: string, cause?: Error) {
    super(message, { cause });
    this.name = this.constructor.name;
    this.cause = cause;

    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, this.constructor);
    }
  }
}
