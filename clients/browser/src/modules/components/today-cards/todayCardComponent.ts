import { createNode } from '../../helpers/createNode';

type TodayCard = HTMLElement;
type TodayCardItems = Array<HTMLElement>;

export class TodayCardComponent {
  todayCardContainer: TodayCard;

  constructor() {
    this.todayCardContainer = createNode('div', 'today-card');
  }

  render(title: string, description = 'Click to add description'): TodayCard {
    const cardItems: TodayCardItems = [
      createNode('h4', 'today-card__title', title),
      createNode('p', 'today-card__description', description),
    ];
    cardItems.forEach(card => {
      this.todayCardContainer.append(card);
    });
    return this.todayCardContainer;
  }
}
