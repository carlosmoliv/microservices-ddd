import { randomUUID } from 'crypto';

export abstract class DomainEvent {
  public readonly occurredOn: Date;
  public readonly eventId: string;
  public readonly eventVersion: number;

  constructor() {
    this.occurredOn = new Date();
    this.eventId = this.generateEventId();
    this.eventVersion = 1;
  }

  private generateEventId(): string {
    return randomUUID();
  }
}
