import { get } from '../API/get';
import { showTodayCards } from '../user-interaction/today-cards/showTodayCards';

export const loadTodayCards = () => {
  get()
    .then(response => {
      if (response.status === 200) {
        return response.json();
      } else {
        alert('Error! Cards for today were not loaded...');
      }
    })
    .then(data => showTodayCards(data.cards))
    .catch(() => alert('Error! Cards for today were not loaded...'));
};
