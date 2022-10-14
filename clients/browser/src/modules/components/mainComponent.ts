import { createNode } from '../helpers/createNode';
import {
  TodayCardsComponent,
  TodayCardsContainer,
} from './today-cards/todayCardsComponent';

export type MainComponent = HTMLElement;

export const renderMainComponent = async (): Promise<MainComponent> => {
  const mainComponent: MainComponent = createNode('main', 'main');

  const renderedTodayCards: Promise<TodayCardsContainer> =
    new TodayCardsComponent().render();

  mainComponent.append(await renderedTodayCards);

  return mainComponent;
};
