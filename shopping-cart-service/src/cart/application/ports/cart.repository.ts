import { Cart } from '../../domain/cart';
import { Injectable } from '@nestjs/common';

@Injectable()
export abstract class CartRepository {
  abstract findByUserId(userId: string): Promise<Cart>;
  abstract save(cart: Cart): Promise<void>;
}
