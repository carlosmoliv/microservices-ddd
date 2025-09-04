import { CartRepository } from '../../../application/ports/cart.repository';
import { Cart } from '../../../domain/cart';
import { Injectable } from '@nestjs/common';

@Injectable()
export class InMemoryCartRepository implements CartRepository {
  private carts = new Map<string, Cart>();

  async findByUserId(userId: string): Promise<Cart> {
    const existingCart = this.carts.get(userId);
    if (existingCart) {
      return existingCart;
    }

    const newCart = Cart.create(userId);
    this.carts.set(userId, newCart);
    return newCart;
  }

  async save(cart: Cart): Promise<void> {
    const map = new Map(this.carts);

    this.carts.set(cart.userId, cart);
  }

  clear(): void {
    this.carts.clear();
  }
}
