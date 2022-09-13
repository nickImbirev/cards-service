import {
  TodayCardsComponent,
  TodayCardsContainer,
} from './today-cards/todayCardsComponent';

type Page = HTMLBodyElement;

export const renderPage = (): void => {
  const page = document.querySelector('body') as Page;
  const renderedTodayCards: TodayCardsContainer =
    new TodayCardsComponent().render();
  page.append(renderedTodayCards);
};
