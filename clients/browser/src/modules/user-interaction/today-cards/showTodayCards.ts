import { collectCardContent } from '../helpers/collectCardContent';

const todayCardsContainer = document.querySelector('.today-cards') as HTMLDivElement;

export const showTodayCards = (todayCardsList: [string]): void => {
  todayCardsList.forEach(todayCard => {
    todayCardsContainer.append(collectCardContent(todayCard));
  });
};
