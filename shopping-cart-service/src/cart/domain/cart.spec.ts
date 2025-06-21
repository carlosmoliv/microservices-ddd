import { Quantity } from './value-objects/quantity';
import { Money } from './value-objects/money';
import { ProductId } from './value-objects/product-id';
import { UserId } from './value-objects/user-id';
import { Cart } from './cart';
import { CartId } from './value-objects/cart-id';
import { ItemAddedToCartEvent } from './events/item-added-to-cart.event';

describe('Cart Aggregate', () => {
  let userId: UserId;
  let productId: ProductId;
  let anotherProductId: ProductId;
  let price: Money;
  let quantity: Quantity;

  beforeEach(() => {
    userId = new UserId('user-123');
    productId = new ProductId('product-456');
    anotherProductId = new ProductId('product-789');
    price = new Money({ amount: 29.99, currency: 'USD' });
    quantity = new Quantity(2);
  });

  describe('create', () => {
    it('should create a new empty cart', () => {
      // Act
      const cart = Cart.create(userId);

      // Assert
      expect(cart.userId).toEqual(userId);
      expect(cart.items).toHaveLength(0);
      expect(cart.itemCount).toBe(0);
      expect(cart.isEmpty).toBe(true);
      expect(cart.id).toBeInstanceOf(CartId);
    });
  });

  describe('addItem', () => {
    it('should add a new item to empty cart', () => {
      // Arrange
      const cart = Cart.create(userId);
      const productName = 'Awesome Product';

      // Act
      cart.addItem(productId, productName, price, quantity);

      // Assert
      expect(cart.items).toHaveLength(1);
      expect(cart.itemCount).toBe(1);
      expect(cart.isEmpty).toBe(false);

      const addedItem = cart.items[0];
      expect(addedItem.productId).toEqual(productId);
      expect(addedItem.productName).toBe(productName);
      expect(addedItem.price).toEqual(price);
      expect(addedItem.quantity).toEqual(quantity);
    });

    it('should add multiple different items to cart', () => {
      // Arrange
      const cart = Cart.create(userId);
      const firstProduct = 'First Product';
      const secondProduct = 'Second Product';
      const secondPrice = new Money({ amount: 19.99, currency: 'USD' });
      const secondQuantity = new Quantity(1);

      // Act
      cart.addItem(productId, firstProduct, price, quantity);
      cart.addItem(
        anotherProductId,
        secondProduct,
        secondPrice,
        secondQuantity,
      );

      // Assert
      expect(cart.items).toHaveLength(2);
      expect(cart.itemCount).toBe(2);

      const firstItem = cart.items.find((item) =>
        item.productId.equals(productId),
      );
      const secondItem = cart.items.find((item) =>
        item.productId.equals(anotherProductId),
      );

      expect(firstItem).toBeDefined();
      expect(firstItem!.productName).toBe(firstProduct);
      expect(firstItem!.quantity).toEqual(quantity);

      expect(secondItem).toBeDefined();
      expect(secondItem!.productName).toBe(secondProduct);
      expect(secondItem!.quantity).toEqual(secondQuantity);
    });

    it('should update quantity when adding existing item', () => {
      // Arrange
      const cart = Cart.create(userId);
      const productName = 'Existing Product';
      const initialQuantity = new Quantity(1);
      const additionalQuantity = new Quantity(3);

      // Mock the updateQuantity method on CartItem
      cart.addItem(productId, productName, price, initialQuantity);
      const existingItem = cart.items[0];
      const updateQuantitySpy = jest.spyOn(existingItem, 'updateQuantity');

      // Act
      cart.addItem(productId, productName, price, additionalQuantity);

      // Assert
      expect(cart.items).toHaveLength(1); // Still only one item
      expect(updateQuantitySpy).toHaveBeenCalledWith(additionalQuantity);
      updateQuantitySpy.mockRestore();
    });

    it('should emit ItemAddedToCartEvent when item is added', () => {
      // Arrange
      const cart = Cart.create(userId);
      const productName = 'Event Test Product';

      // Act
      cart.addItem(productId, productName, price, quantity);

      // Assert
      const domainEvents = cart.getDomainEvents();
      expect(domainEvents).toHaveLength(1);
      expect(domainEvents[0]).toBeInstanceOf(ItemAddedToCartEvent);

      const event = domainEvents[0] as ItemAddedToCartEvent;
      expect(event.cartId).toEqual(cart.id);
      expect(event.productId).toEqual(productId);
      expect(event.quantity).toEqual(quantity);
    });

    it('should emit ItemAddedToCartEvent when updating existing item quantity', () => {
      // Arrange
      const cart = Cart.create(userId);
      const productName = 'Existing Product';
      const initialQuantity = new Quantity(1);
      const additionalQuantity = new Quantity(2);

      cart.addItem(productId, productName, price, initialQuantity);
      cart.clearDomainEvents();

      // Act
      cart.addItem(productId, productName, price, additionalQuantity);

      // Assert
      const domainEvents = cart.getDomainEvents();
      expect(domainEvents).toHaveLength(1);
      expect(domainEvents[0]).toBeInstanceOf(ItemAddedToCartEvent);

      const event = domainEvents[0] as ItemAddedToCartEvent;
      expect(event.quantity).toEqual(additionalQuantity);
    });

    it('should return new array instance each time (defensive copy)', () => {
      // Arrange
      const cart = Cart.create(userId);
      const productName = 'Immutable Test Product';

      // Act
      cart.addItem(productId, productName, price, quantity);
      const items1 = cart.items;
      const items2 = cart.items;

      // Assert
      expect(items1).not.toBe(items2);
      expect(items1).toEqual(items2);
    });

    it('should not affect internal array when external array is modified', () => {
      // Arrange
      const cart = Cart.create(userId);
      const productName = 'Defensive Copy Test';

      // Act
      cart.addItem(productId, productName, price, quantity);
      const items = cart.items;
      const originalLength = cart.itemCount;

      (items as any).push('fake item');

      expect(cart.itemCount).toBe(originalLength);
      expect(cart.items).toHaveLength(originalLength);
      expect(cart.items).not.toContain('fake item');
    });

    it('should update updatedAt timestamp when item is added', () => {
      // Arrange
      const cart = Cart.create(userId);
      const productName = 'Timestamp Test Product';
      const originalUpdatedAt = (cart as any)._updatedAt;

      jest.advanceTimersByTime(100);

      // Act
      cart.addItem(productId, productName, price, quantity);

      // Assert
      const newUpdatedAt = (cart as any)._updatedAt;
      expect(newUpdatedAt).not.toEqual(originalUpdatedAt);
      expect(newUpdatedAt.getTime()).toBeGreaterThan(
        originalUpdatedAt.getTime(),
      );
    });
  });
});
