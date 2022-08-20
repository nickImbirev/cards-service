import { createNewCard } from '../API/new-card/createNewCard';
import { getDataForNewCard } from '../user-interaction/new-card/getDataForNewCard';
import { showNewCardResult } from '../user-interaction/new-card/showNewCardResult';

const newCardInput = document.querySelector('.new-card__input') as HTMLInputElement;
const newCardButton = document.querySelector('.new-card__button') as HTMLButtonElement;

newCardButton.addEventListener('click', () => {
  const userData: string = getDataForNewCard(newCardInput);

  createNewCard(userData)
    .then(response => showNewCardResult(response.status))
    .catch(() => alert('Error! New card was not created...'));
});
