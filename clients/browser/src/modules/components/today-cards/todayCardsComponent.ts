import { createNode } from '../../helpers/createNode';
import { renderTodayCard } from './todayCardComponent';
import { saveTodayCardsToLocalStorage } from '../../helpers/saveTodayCardsToLocalStorage';
import { ErrorContainer, renderErrorContainer } from '../errorMessageComponent';

export type TodayCardsContainer = HTMLElement;
type TodayCardsList = Array<string>;

export class TodayCardsComponent {
  constructor() {
    saveTodayCardsToLocalStorage();
  }

  render(): TodayCardsContainer {
    const todayCardsContainer: TodayCardsContainer = createNode(
      'main',
      'today-cards'
    );

    const localStorageData = localStorage.getItem('todayCards') as string;

    if (localStorageData) {
      const todayCardsList: TodayCardsList = JSON.parse(localStorageData);

      todayCardsList.forEach((todayCardData) => {
        const renderedTodayCard = renderTodayCard(todayCardData);
        todayCardsContainer.append(renderedTodayCard);
      });
    } else {
      const errorContainer: ErrorContainer = renderErrorContainer(
        'Sorry, cards for today were not loaded...'
      );
      todayCardsContainer.append(errorContainer);
    }

    return todayCardsContainer;
  }
}
