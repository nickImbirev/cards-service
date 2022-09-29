import { createNode } from '../helpers/createNode';
import {
  TodayCardsContainer,
  TodayCardsComponent,
} from './today-cards/todayCardsComponent';

export type Button = HTMLElement;

const updateTodayCards = async (): Promise<void> => {
  const main = document.querySelector('.main');
  const todayCardsContainer = document.querySelector('.today-cards');
  if (main) {
    if (todayCardsContainer) main.removeChild(todayCardsContainer);

    const renderedTodayCards: Promise<TodayCardsContainer> =
      new TodayCardsComponent().render();

    main.append(await renderedTodayCards);
  }
};

export const renderUpdateTodayCardsButton = (): Button => {
  const renderedUpdateTodayCardsBtn: Button = createNode(
    'button',
    'btn btn_updateTodayCards',
    'Update cards for today'
  );
  renderedUpdateTodayCardsBtn.addEventListener('click', updateTodayCards);
  return renderedUpdateTodayCardsBtn;
};
