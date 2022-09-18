import { get } from '../../API/get';
import { createNode } from '../../helpers/createNode';
import { renderTodayCard } from './todayCardComponent';
import { ErrorContainer, renderErrorContainer } from '../errorMessageComponent';

export type TodayCardsContainer = HTMLElement;
type TodayCardsList = Array<string>;
type ResponseBody = {
  cards: TodayCardsList;
};

export class TodayCardsComponent {
  async render(): Promise<TodayCardsContainer> {
    const todayCardsContainer: TodayCardsContainer = createNode(
      'main',
      'today-cards'
    );

    let response: Response;
    let data: ResponseBody | undefined;

    try {
      response = await get();
      data = await response.json();
    } catch (error) {
      const errorContainer: ErrorContainer = renderErrorContainer(
        `Sorry, cards for today were not loaded because of ${error}.`
      );
      todayCardsContainer.append(errorContainer);
    }

    if (data) {
      const todayCardsList: TodayCardsList = data.cards;
      todayCardsList.forEach((todayCardData) => {
        const renderedTodayCard = renderTodayCard(todayCardData);
        todayCardsContainer.append(renderedTodayCard);
      });
    }
    return todayCardsContainer;
  }
}
