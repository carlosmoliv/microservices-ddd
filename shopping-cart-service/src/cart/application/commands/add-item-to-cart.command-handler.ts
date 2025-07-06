import { lastValueFrom } from 'rxjs';
import { Inject } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { CommandHandler, ICommandHandler, EventPublisher } from '@nestjs/cqrs';
import { AddItemToCartCommand } from './add-item-to-cart.command';
import { CartRepository } from '../ports/cart.repository';
import { Cart } from '../../domain/cart';
import { Money } from '../../domain/value-objects/money';
import { Quantity } from '../../domain/value-objects/quantity';

@CommandHandler(AddItemToCartCommand)
export class AddItemToCartCommandHandler
  implements ICommandHandler<AddItemToCartCommand>
{
  constructor(
    private readonly eventPublisher: EventPublisher,
    private readonly cartRepository: CartRepository,
    @Inject('PRODUCT_RPC_SERVICE')
    private readonly productService: ClientProxy,
  ) {}

  async execute(command: AddItemToCartCommand): Promise<void> {
    const { userId, productId, quantity } = command;

    let cart = await this.cartRepository.findByUserId(userId);
    if (!cart) {
      cart = Cart.create(userId);
    }

    const productInfo = await lastValueFrom(
      this.productService.send('product.get_details_with_stock', {
        productId,
        requiredQuantity: quantity,
      }),
    );
    if (!productInfo) {
      throw new Error(`Product with ID ${productId} not found.`);
    }
    if (!productInfo.hasStock) {
      throw new Error('Insufficient stock');
    }

    const mergedCart = this.eventPublisher.mergeObjectContext(cart);
    mergedCart.addItem(
      productId,
      productInfo.product.name,
      new Money({ amount: productInfo.product.stockQuantity, currency: 'USD' }),
      new Quantity(quantity),
    );

    await this.cartRepository.save(mergedCart);

    mergedCart.commit();
  }
}
