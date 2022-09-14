import { get } from '../../API/get';
import { createNode } from '../../helpers/createNode';
import { renderTodayCard } from './todayCardComponent';
import { ErrorContainer, renderErrorContainer } from '../errorMessageComponent';

export type TodayCardsContainer = HTMLElement;
type TodayCardsList = Array<string>;

export class TodayCardsComponent {
  async render(): Promise<TodayCardsContainer> {
    const todayCardsContainer: TodayCardsContainer = createNode(
      'main',
      'today-cards'
    );
    const response = await get();
    const data = await response.json();

    if (data) {
      const todayCardsList: TodayCardsList = data.cards;

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
