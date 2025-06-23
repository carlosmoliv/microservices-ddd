import { Quantity } from './value-objects/quantity';
import { Money } from './value-objects/money';
import { Cart } from './cart';
import { ItemAddedToCartEvent } from './events/item-added-to-cart.event';

describe('Cart Aggregate Root', () => {
  let userId: string;
  let productId: string;
  let anotherProductId: string;
  let price: Money;
  let quantity: Quantity;

  beforeEach(() => {
    userId = 'user-123';
    productId = 'product-456';
    anotherProductId = 'product-789';
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
      expect(cart.id).toBeDefined();
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

      const addedItem = cart.items[0];
      expect(addedItem).toMatchObject({
        productId,
        productName,
        price,
        quantity,
      });
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

      const firstItem = cart.items.find((item) => item.productId === productId);
      const secondItem = cart.items.find(
        (item) => item.productId === anotherProductId,
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
      const domainEvents = cart.getUncommittedEvents();
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
      cart.uncommit();

      // Act
      cart.addItem(productId, productName, price, additionalQuantity);

      // Assert
      const domainEvents = cart.getUncommittedEvents();
      expect(domainEvents).toHaveLength(1);
      expect(domainEvents[0]).toBeInstanceOf(ItemAddedToCartEvent);

      const event = domainEvents[0] as ItemAddedToCartEvent;
      expect(event.quantity).toEqual(additionalQuantity);
    });
  });
});
