import { Money } from './value-objects/money';
import { Quantity } from './value-objects/quantity';
import { CartItem } from './cart-item';

describe('CartItem', () => {
  let productId: string;
  let productName: string;
  let price: Money;
  let quantity: Quantity;

  beforeEach(() => {
    productId = 'product-123';
    productName = 'Test Product';
    price = new Money({ amount: 25.99, currency: 'USD' });
    quantity = new Quantity(3);
  });

  describe('create', () => {
    it('should create a new cart item with all properties', () => {
      // Act
      const cartItem = CartItem.create(productId, productName, price, quantity);

      // Assert
      expect(cartItem.id).toBeDefined();
      expect(cartItem.productId).toEqual(productId);
      expect(cartItem.productName).toBe(productName);
      expect(cartItem.price).toEqual(price);
      expect(cartItem.quantity).toEqual(quantity);
    });

    it('should generate unique IDs for different cart items', () => {
      // Act
      const item1 = CartItem.create(productId, productName, price, quantity);
      const item2 = CartItem.create(productId, productName, price, quantity);

      // Assert
      expect(item1.id).not.toEqual(item2.id);
    });
  });

  describe('updateQuantity', () => {
    it('should update the quantity successfully', () => {
      // Arrange
      const cartItem = CartItem.create(productId, productName, price, quantity);
      const newQuantity = new Quantity(5);

      // Act
      cartItem.updateQuantity(newQuantity);

      // Assert
      expect(cartItem.quantity).toEqual(newQuantity);
    });

    it('should allow updating quantity multiple times', () => {
      // Arrange
      const cartItem = CartItem.create(productId, productName, price, quantity);
      const firstUpdate = new Quantity(7);
      const secondUpdate = new Quantity(2);

      // Act
      cartItem.updateQuantity(firstUpdate);
      cartItem.updateQuantity(secondUpdate);

      // Assert
      expect(cartItem.quantity).toEqual(secondUpdate);
    });
  });

  describe('updatePrice', () => {
    it('should update the price successfully', () => {
      // Arrange
      const cartItem = CartItem.create(productId, productName, price, quantity);
      const newPrice = new Money({ amount: 19.99, currency: 'USD' });

      // Act
      cartItem.updatePrice(newPrice);

      // Assert
      expect(cartItem.price).toEqual(newPrice);
    });

    it('should allow updating price to zero', () => {
      // Arrange
      const cartItem = CartItem.create(productId, productName, price, quantity);
      const freePrice = new Money({ amount: 0, currency: 'USD' });

      // Act
      cartItem.updatePrice(freePrice);

      // Assert
      expect(cartItem.price).toEqual(freePrice);
    });

    it('should allow updating price multiple times', () => {
      // Arrange
      const cartItem = CartItem.create(productId, productName, price, quantity);
      const firstPrice = new Money({ amount: 15.0, currency: 'USD' });
      const secondPrice = new Money({ amount: 30.0, currency: 'USD' });

      // Act
      cartItem.updatePrice(firstPrice);
      cartItem.updatePrice(secondPrice);

      // Assert
      expect(cartItem.price).toEqual(secondPrice);
    });
  });

  describe('subtotal', () => {
    it('should calculate subtotal correctly', () => {
      // Arrange
      const unitPrice = new Money({ amount: 10.5, currency: 'USD' });
      const qty = new Quantity(4);
      const cartItem = CartItem.create(productId, productName, unitPrice, qty);

      // Act
      const subtotal = cartItem.subtotal();

      // Assert
      const expectedSubtotal = new Money({ amount: 42.0, currency: 'USD' });
      expect(subtotal).toEqual(expectedSubtotal);
    });

    it('should return zero subtotal when quantity is zero', () => {
      // Arrange
      const unitPrice = new Money({ amount: 15.99, currency: 'USD' });
      const zeroQty = new Quantity(0);
      const cartItem = CartItem.create(
        productId,
        productName,
        unitPrice,
        zeroQty,
      );

      // Act
      const subtotal = cartItem.subtotal();

      // Assert
      const expectedSubtotal = new Money({ amount: 0, currency: 'USD' });
      expect(subtotal).toEqual(expectedSubtotal);
    });

    it('should return zero subtotal when price is zero', () => {
      // Arrange
      const freePrice = new Money({ amount: 0, currency: 'USD' });
      const qty = new Quantity(5);
      const cartItem = CartItem.create(productId, productName, freePrice, qty);

      // Act
      const subtotal = cartItem.subtotal();

      // Assert
      const expectedSubtotal = new Money({ amount: 0, currency: 'USD' });
      expect(subtotal).toEqual(expectedSubtotal);
    });

    it('should calculate subtotal with decimal quantities', () => {
      // Arrange
      const unitPrice = new Money({ amount: 12.33, currency: 'USD' });
      const decimalQty = new Quantity(2.5);
      const cartItem = CartItem.create(
        productId,
        productName,
        unitPrice,
        decimalQty,
      );

      // Act
      const subtotal = cartItem.subtotal();

      // Assert
      const expectedSubtotal = new Money({ amount: 30.825, currency: 'USD' });
      expect(subtotal).toEqual(expectedSubtotal);
    });

    it('should recalculate subtotal after quantity update', () => {
      // Arrange
      const unitPrice = new Money({ amount: 8.0, currency: 'USD' });
      const initialQty = new Quantity(3);
      const cartItem = CartItem.create(
        productId,
        productName,
        unitPrice,
        initialQty,
      );

      const initialSubtotal = cartItem.subtotal();
      expect(initialSubtotal).toEqual(
        new Money({ amount: 24.0, currency: 'USD' }),
      );

      // Act
      const newQty = new Quantity(6);
      cartItem.updateQuantity(newQty);
      const newSubtotal = cartItem.subtotal();

      // Assert
      const expectedSubtotal = new Money({ amount: 48.0, currency: 'USD' });
      expect(newSubtotal).toEqual(expectedSubtotal);
    });

    it('should recalculate subtotal after price update', () => {
      // Arrange
      const initialPrice = new Money({ amount: 5.0, currency: 'USD' });
      const qty = new Quantity(4);
      const cartItem = CartItem.create(
        productId,
        productName,
        initialPrice,
        qty,
      );

      const initialSubtotal = cartItem.subtotal();
      expect(initialSubtotal).toEqual(
        new Money({ amount: 20.0, currency: 'USD' }),
      );

      // Act
      const newPrice = new Money({ amount: 7.5, currency: 'USD' });
      cartItem.updatePrice(newPrice);
      const newSubtotal = cartItem.subtotal();

      // Assert
      const expectedSubtotal = new Money({ amount: 30.0, currency: 'USD' });
      expect(newSubtotal).toEqual(expectedSubtotal);
    });
  });

  describe('immutability of readonly properties', () => {
    it('should not allow modification of readonly properties', () => {
      // Arrange
      const cartItem = CartItem.create(productId, productName, price, quantity);
      const originalId = cartItem.id;
      const originalProductId = cartItem.productId;

      // Act & Assert
      cartItem.updateQuantity(new Quantity(10));
      cartItem.updatePrice(new Money({ amount: 99.99, currency: 'USD' }));

      expect(cartItem.id).toBe(originalId);
      expect(cartItem.productId).toBe(originalProductId);
    });
  });
});
