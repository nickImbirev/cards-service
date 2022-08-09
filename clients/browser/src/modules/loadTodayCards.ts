import { collectCardContent } from '../helpers/collectCardContent';
import { sendRequest } from '../helpers/sendRequest';

const todayCardsContainer = document.querySelector('.today-cards') as HTMLDivElement;
const todayCardsURL = 'http://localhost:8081/today/cards';

const loadTodayCards = () => {
  sendRequest(todayCardsURL)
    .then(response => {
      if (response.status === 200) {
        return response.json();
      } else {
        alert('Error! Cards list was not loaded...');
      }
    })
    .then(data => {
      const todayCards: [string] = data.cards;
      todayCards.forEach(card => {
        const cardNode = collectCardContent(card);
        todayCardsContainer.append(cardNode);
      });
    })
    .catch(() => alert('Error! Cards list was not loaded...'));
};

loadTodayCards();
