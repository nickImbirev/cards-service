import { createNode } from '../../helpers/createNode';
import { TodayCardComponent } from './todayCardComponent';
import { get } from '../../API/get';
import { ErrorMessageComponent } from '../errorMessageComponent';

type TodayCardsContainer = HTMLElement;
type TodayCardsList = Array<string>;
type ParentComponent = HTMLElement;

export class TodayCardsComponent {
  todayCardsContainer: TodayCardsContainer;

  errorMessage: string;

  constructor(parent: ParentComponent) {
    this.todayCardsContainer = createNode('main', 'today-cards');
    this.errorMessage = 'I am sorry, cards for today were not loaded...';

    this.load(parent);
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
          new ErrorMessageComponent(this.errorMessage).render(this.todayCardsContainer);
        }
      })
      .then(data => {
        const todayCardsContainer: TodayCardsContainer = this.render(data.cards);
        parent.append(todayCardsContainer);
      })
      .catch(() => new ErrorMessageComponent(this.errorMessage).render(this.todayCardsContainer));
  }
}
