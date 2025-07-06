import { Controller, Post, Body, Param } from '@nestjs/common';
import { CommandBus } from '@nestjs/cqrs';
import { AddItemToCartCommand } from '../../application/commands/add-item-to-cart.command';

@Controller('api/carts')
export class CartController {
  constructor(private readonly commandBus: CommandBus) {}

  @Post(':userId/items')
  async addItemToCart(
    @Param('userId') userId: string,
    @Body() body: { productId: string; quantity: number },
  ): Promise<void> {
    const command = new AddItemToCartCommand(
      userId,
      body.productId,
      body.quantity,
    );
    await this.commandBus.execute(command);
  }
}
