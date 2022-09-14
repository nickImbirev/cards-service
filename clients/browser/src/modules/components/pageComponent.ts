import {
  TodayCardsComponent,
  TodayCardsContainer,
} from './today-cards/todayCardsComponent';

export const renderPage = async (): Promise<void> => {
  const renderedTodayCards: Promise<TodayCardsContainer> =
    new TodayCardsComponent().render();
  document.body.append(await renderedTodayCards);
};
