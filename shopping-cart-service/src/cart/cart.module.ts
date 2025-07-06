import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { CqrsModule } from '@nestjs/cqrs';
import { AddItemToCartCommandHandler } from './application/commands/add-item-to-cart.command-handler';
import { ItemAddedToCartEventHandler } from './application/event-handlers/item-added-to-cart.event-handler';
import { CartRepository } from './application/ports/cart.repository';
import { InMemoryCartRepository } from './infrastructure/persistence/in-memory/in-memory-cart.repository';
import { CartController } from './presenters /controllers/cart.controller';
import { ConfigService } from '@nestjs/config';

@Module({
  imports: [
    CqrsModule.forRoot(),
    ClientsModule.registerAsync([
      {
        name: 'PRODUCT_RPC_SERVICE',
        useFactory: (configService: ConfigService) => ({
          transport: Transport.RMQ,
          options: {
            urls: [configService.getOrThrow<string>('RABBITMQ_URI')],
            queue: 'product_queue',
            queueOptions: { durable: false },
          },
        }),
        inject: [ConfigService],
      },
      {
        name: 'CART_EVENTS_PUBLISHER',
        useFactory: (configService: ConfigService) => ({
          transport: Transport.RMQ,
          options: {
            urls: [configService.getOrThrow<string>('RABBITMQ_URI')],
            exchange: 'cart_events_exchange',
            exchangeOptions: { type: 'topic', durable: true },

            // TODO: queue is not needed here, but required by the library. Find a way to avoid it.
            queue: 'cart_events_queue',
            queueOptions: { durable: true },
          },
        }),
        inject: [ConfigService],
      },
    ]),
  ],
  providers: [
    AddItemToCartCommandHandler,
    ItemAddedToCartEventHandler,
    {
      provide: CartRepository,
      useClass: InMemoryCartRepository,
    },
  ],
  controllers: [CartController],
})
export class CartModule {}
