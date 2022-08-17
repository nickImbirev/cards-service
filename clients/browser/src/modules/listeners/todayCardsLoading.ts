import { getTodayCards } from '../API/today-cards/getTodayCards';
import { isStatusOk } from '../user-interaction/today-cards/isStatusOk';
import { showTodayCards } from '../user-interaction/today-cards/showTodayCards';


window.addEventListener('load', () => {
  getTodayCards()
    .then(response => {
      if (isStatusOk(response.status)) {
        return response.json();
      } else {
        alert('Error! Cards for today were not loaded...');
      }
    })
    .then(data => showTodayCards(data.cards))
    .catch(() => alert('Error! Cards for today were not loaded...'));
});
