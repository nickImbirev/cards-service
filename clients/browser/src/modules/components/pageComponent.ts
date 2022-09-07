import { TodayCardsComponent } from './today-cards/todayCardsComponent';

type Page = HTMLBodyElement;

export const renderPage = (): void => {
  const page: Page = document.querySelector('body') as HTMLBodyElement;
  new TodayCardsComponent(page);
};
