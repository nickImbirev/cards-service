import { createNode } from '../helpers/createNode';

type MessageComponent = HTMLElement;
type ParentComponent = HTMLElement;

export class ErrorMessageComponent {
  messageComponent: MessageComponent;

  constructor(message: string) {
    this.messageComponent = createNode('p', 'error-message', message);
  }

  render(parent: ParentComponent) {
    parent.append(this.messageComponent);
  }
}
