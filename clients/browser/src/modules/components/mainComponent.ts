import { createNode } from '../helpers/createNode';
import {
  TodayCardsComponent,
  TodayCardsContainer,
} from './today-cards/todayCardsComponent';
import {
  Button,
  renderUpdateTodayCardsButton,
} from './updateTodayCardsBtnComponent';

export type MainComponent = HTMLElement;

export const renderMainComponent = async (): Promise<MainComponent> => {
  const mainComponent: MainComponent = createNode('main', 'main');

  const renderedTodayCards: Promise<TodayCardsContainer> =
    new TodayCardsComponent().render();
  const renderedUpdateTodayCardsBtn: Button = renderUpdateTodayCardsButton();

  mainComponent.append(await renderedTodayCards);
  mainComponent.append(renderedUpdateTodayCardsBtn);

  return mainComponent;
};
