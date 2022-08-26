import { createNode } from '../../user-interaction/helpers/createNode';
import { TodayCardComponent } from './todayCardComponent';
import { get } from '../../API/get';

type TodayCardsContainer = HTMLElement;
type TodayCardsList = Array<string>;
type ParentComponent = HTMLElement;

export class TodayCardsComponent {
  todayCardsContainer: TodayCardsContainer;

  constructor() {
    this.todayCardsContainer = createNode('main', 'today-cards');
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
      .then(response => {
        if (response.status === 200) {
          return response.json();
        } else {
          alert('Error! Cards for today were not loaded...');
        }
      })
      .then(data => {
        const todayCardsContainer: TodayCardsContainer = this.render(data.cards);
        parent.append(todayCardsContainer);
      })
      .catch(() => alert('Error! Cards for today were not loaded...'));
  }
}
