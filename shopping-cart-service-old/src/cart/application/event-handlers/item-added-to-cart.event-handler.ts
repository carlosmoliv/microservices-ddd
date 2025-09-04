import { Inject } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { EventsHandler, IEventHandler } from '@nestjs/cqrs';
import { ItemAddedToCartEvent } from '../../domain/events/item-added-to-cart.event';

@EventsHandler(ItemAddedToCartEvent)
export class ItemAddedToCartEventHandler
  implements IEventHandler<ItemAddedToCartEvent>
{
  constructor(
    @Inject('CART_EVENTS_PUBLISHER')
    private readonly eventPublisherClient: ClientProxy,
  ) {}

  async handle(event: ItemAddedToCartEvent): Promise<void> {
    const message = {
      cartId: event.cartId,
      productId: event.productId,
      quantity: event.quantity.value,
      timestamp: new Date().toISOString(),
    };
    this.eventPublisherClient.emit('cart.item.added', message);
  }
}
