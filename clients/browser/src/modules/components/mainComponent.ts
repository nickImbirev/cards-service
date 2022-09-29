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
type MainChildComponents = [Promise<TodayCardsContainer>, Button];

export const renderMainComponent = async (): Promise<MainComponent> => {
  const mainComponent: MainComponent = createNode('main', 'main');

  const renderedTodayCards: Promise<TodayCardsContainer> =
    new TodayCardsComponent().render();
  const renderedUpdateTodayCardsBtn: Button = renderUpdateTodayCardsButton();

  const mainComponents: MainChildComponents = [
    renderedTodayCards,
    renderedUpdateTodayCardsBtn,
  ];
  mainComponents.forEach(async (component) =>
    mainComponent.append(await component)
  );

  return mainComponent;
};
