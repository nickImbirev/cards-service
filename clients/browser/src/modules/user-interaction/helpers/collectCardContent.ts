import { createNode } from './createNode';

export const collectCardContent = (title: string, description = ''): HTMLElement => {
  const cardContainer: HTMLElement = createNode('div', 'today-card');
  const cardButtonsContainer: HTMLElement = createNode('div', 'today-card__buttons');
  const cardButtons: Array<HTMLElement> = [
    createNode('button', 'today-card__button_edit', 'Edit card'),
    createNode('button', 'today-card__button_delete', 'Delete card'),
  ];
  cardButtons.forEach(button => {
    cardButtonsContainer.append(button);
  });
  const cardItems: Array<HTMLElement> = [
    createNode('h4', 'today-card__title', title),
    createNode('p', 'today-card__description', description),
    cardButtonsContainer,
  ];
  cardItems.forEach(item => {
    cardContainer.append(item);
  });
  return cardContainer;
};
