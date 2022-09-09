import { get } from '../API/get';

export const saveTodayCardsToLocalStorage = (): void => {
  get()
    .then(response => response.json())
    .then(data => {
      localStorage.setItem('todayCards', JSON.stringify(data.cards));
    })
    .catch(() => {
      localStorage.setItem('todayCards', '');
    });
};
