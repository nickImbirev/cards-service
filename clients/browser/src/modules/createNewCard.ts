import { sendRequest } from '../helpers/sendRequest';

const newCardInput = document.querySelector('.new-card__input') as HTMLInputElement;
const newCardButton = document.querySelector('.new-card__button') as HTMLButtonElement;
const newCardURL = 'http://localhost:8081/card';

const createNewCard = () => {
  const title = newCardInput.value;
  sendRequest(newCardURL, 'POST', { title: title })
    .then(response => {
      if (response.status === 201) {
        alert('New card was created!');
      } else {
        alert('Error! New card was not created...');
      }
    })
    .catch(() => alert('Error! New card was not created...'));
};

newCardButton.addEventListener('click', createNewCard);
