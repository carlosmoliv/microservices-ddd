export abstract class ValueObject<T> {
  protected readonly _value: T;

  constructor(value: T) {
    this._value = Object.freeze(value);
  }

  get value(): T {
    return this._value;
  }

  equals(other: ValueObject<T>): boolean {
    if (other === null || other === undefined) {
      return false;
    }

    if (this.constructor !== other.constructor) {
      return false;
    }

    return this.deepEquals(this._value, other._value);
  }

  toString(): string {
    return JSON.stringify(this._value);
  }

  private deepEquals(a: any, b: any): boolean {
    if (a === b) return true;

    if (a instanceof Date && b instanceof Date) {
      return a.getTime() === b.getTime();
    }

    if (!a || !b || (typeof a !== 'object' && typeof b !== 'object')) {
      return a === b;
    }

    if (a === null || a === undefined || b === null || b === undefined) {
      return false;
    }

    if (a.prototype !== b.prototype) return false;

    const keys = Object.keys(a);
    if (keys.length !== Object.keys(b).length) {
      return false;
    }

    return keys.every((k) => this.deepEquals(a[k], b[k]));
  }
}
