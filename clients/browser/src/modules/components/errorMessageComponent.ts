import { createNode } from '../helpers/createNode';

export type ErrorContainer = HTMLElement;

export const renderErrorContainer = (message: string): ErrorContainer =>
  createNode('p', 'error-message', message);
