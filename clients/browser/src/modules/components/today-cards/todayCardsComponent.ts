import { createNode } from '../../helpers/createNode';
import { TodayCardComponent } from './todayCardComponent';
import { get } from '../../API/get';
import { renderErrorContainer } from '../errorMessageComponent';

type TodayCardsContainer = HTMLElement;
type TodayCardsList = Array<string>;
type ParentComponent = HTMLElement;

export class TodayCardsComponent {
  todayCardsContainer: TodayCardsContainer;

  constructor(parent: ParentComponent) {
    this.todayCardsContainer = createNode('main', 'today-cards');

    this.load(parent);

    this.todayCardsContainer.addEventListener('click', () => this.update(parent));
  }

  render(todayCardsList: TodayCardsList): TodayCardsContainer {
    todayCardsList.forEach(todayCardData => {
      const todayCard = new TodayCardComponent();
      const renderedTodayCard = todayCard.render(todayCardData);
      this.todayCardsContainer.append(renderedTodayCard);
    });
    return this.todayCardsContainer;
  }

  load(parent: ParentComponent) {
    get()
      .then(response => response.json())
      .then(data => {
        const todayCardsContainer: TodayCardsContainer = this.render(data.cards);
        parent.append(todayCardsContainer);
      })
      .catch(() => {
        const message: string = 'I am sorry, cards for today were not loaded...';
        parent.append(renderErrorContainer(message));
      });
  }

  update(parent: ParentComponent) {
    while (this.todayCardsContainer.firstChild) {
      this.todayCardsContainer.removeChild(this.todayCardsContainer.firstChild);
    }
    this.load(parent);
  }
}
