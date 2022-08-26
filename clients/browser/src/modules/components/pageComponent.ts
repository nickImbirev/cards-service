import { TodayCardsComponent } from './today-cards/todayCardsComponent';

type Page = HTMLBodyElement;

export class PageComponent {
  page: Page;

  constructor() {
    this.page = document.querySelector('body') as HTMLBodyElement;
  }

  load(): void {
    window.addEventListener('load', () => {
      const todayCardsComponent = new TodayCardsComponent();
      todayCardsComponent.load(this.page);
    });
  }
}
