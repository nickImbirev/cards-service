import { createNewCard } from '../API-user/createNewCard';
import { getDataForNewCard } from '../user-interaction/new-card/getDataForNewCard';

const newCardInput = document.querySelector('.new-card__input') as HTMLInputElement;
const newCardButton = document.querySelector('.new-card__button') as HTMLButtonElement;

newCardButton.addEventListener('click', () => {
  const userData: string = getDataForNewCard(newCardInput);
  createNewCard(userData);
});
