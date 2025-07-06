import * as request from 'supertest';
import { faker } from '@faker-js/faker';
import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import { CartModule } from '../../src/cart/cart.module';
import { ConfigModule } from '@nestjs/config';
import { ClientProxy } from '@nestjs/microservices';
import { of } from 'rxjs';
import { CartRepository } from '../../src/cart/application/ports/cart.repository';
import { mock, MockProxy } from 'jest-mock-extended';

describe('Add item to cart e2e Tests', () => {
  let app: INestApplication;
  let moduleFixture: TestingModule;
  let productServiceMock: MockProxy<ClientProxy>;
  let cartEventsPublisherMock: MockProxy<ClientProxy>;
  let cartRepository: CartRepository;

  beforeEach(async () => {
    productServiceMock = mock();
    cartEventsPublisherMock = mock();

    moduleFixture = await Test.createTestingModule({
      imports: [ConfigModule.forRoot({ isGlobal: true }), CartModule],
      providers: [],
    })
      .overrideProvider('PRODUCT_RPC_SERVICE')
      .useValue(productServiceMock)
      .overrideProvider('CART_EVENTS_PUBLISHER')
      .useValue(cartEventsPublisherMock)
      .compile();

    app = moduleFixture.createNestApplication();
    await app.init();

    cartRepository = moduleFixture.get<CartRepository>(CartRepository);
  });

  afterAll(async () => {
    await app.close();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('POST /api/carts/:userId/items', () => {
    it('should add an item to new cart', async () => {
      // Arrange
      const quantity = 2;
      const productId = faker.string.uuid();
      const mockProductInfo = {
        product: {
          id: productId,
          name: 'Test Product',
          price: 29.99,
          stockQuantity: 100,
        },
        hasStock: true,
      };
      const userId = faker.string.uuid();

      productServiceMock.send.mockReturnValue(of(mockProductInfo));

      // Act
      await request(app.getHttpServer())
        .post(`/api/carts/${userId}/items`)
        .send({ productId, quantity })
        .expect(201);

      expect(productServiceMock.send).toHaveBeenCalledWith(
        'product.get_details_with_stock',
        {
          productId,
          requiredQuantity: quantity,
        },
      );

      const savedCart = await cartRepository.findByUserId(userId);
      expect(savedCart.userId).toBe(userId);
      expect(savedCart.items).toHaveLength(1);
      expect(savedCart.items[0].productId).toBe(productId);
      expect(savedCart.items[0].productName).toBe('Test Product');
      expect(savedCart.items[0].quantity.value).toBe(quantity);
    });

    it.todo('should add an item to existing cart');

    it.todo('should return 500 when product service fails');

    it.todo('should handle concurrent requests to same cart');
  });
});
