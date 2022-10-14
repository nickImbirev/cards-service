import { get } from '../../API/get';
import { createNode } from '../../helpers/createNode';
import { renderTodayCard } from './todayCardComponent';
import { ErrorContainer, renderErrorContainer } from '../errorMessageComponent';
import { removeChildren } from '../../helpers/removeChildren';

export type TodayCards = HTMLElement;
export type TodayCardsContainer = HTMLElement;
export type Button = HTMLButtonElement;
type TodayCardsList = Array<string>;
type ResponseBody = {
  cards: TodayCardsList;
};

export class TodayCardsComponent {
  private static todayCards: TodayCards = createNode('div', 'today-cards');
  private static todayCardsContainer: TodayCardsContainer = createNode(
    'div',
    'today-cards__list'
  );

  private static renderTodayCards = async (): Promise<TodayCardsContainer> => {
    let response: Response;
    let data: ResponseBody | undefined;

    const todayCardsContainer: TodayCardsContainer =
      TodayCardsComponent.todayCardsContainer;

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
  };

  private static updateTodayCards = async (): Promise<void> => {
    const todayCardsContainer: TodayCardsContainer =
      TodayCardsComponent.todayCardsContainer;
    removeChildren(todayCardsContainer);
    todayCardsContainer.remove();

    const updatedTodayCardsContainer = TodayCardsComponent.renderTodayCards();
    TodayCardsComponent.todayCards.prepend(await updatedTodayCardsContainer);
  };

  private static todayCardsButton = createNode(
    'button',
    'today-card__button',
    'Update cards for today',
    'click',
    TodayCardsComponent.updateTodayCards
  );

  async render(): Promise<TodayCardsContainer> {
    TodayCardsComponent.todayCards.append(
      await TodayCardsComponent.renderTodayCards(),
      TodayCardsComponent.todayCardsButton
    );
    return TodayCardsComponent.todayCards;
  }
}
