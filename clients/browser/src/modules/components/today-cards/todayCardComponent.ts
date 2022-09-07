import { createNode } from '../../helpers/createNode';

type TodayCard = HTMLElement;
type TodayCardItems = Array<HTMLElement>;

export const renderTodayCard = (title: string, description = 'Click to add description'): TodayCard => {
  const todayCardContainer = createNode('div', 'today-card');
  const cardItems: TodayCardItems = [
    createNode('h4', 'today-card__title', title),
    createNode('p', 'today-card__description', description),
  ];
  cardItems.forEach((card) => {
    todayCardContainer.append(card);
  });
  return todayCardContainer;
};
