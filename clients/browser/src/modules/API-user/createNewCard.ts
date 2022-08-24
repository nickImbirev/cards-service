import { post } from '../API/post';
import { showNewCardResult } from '../user-interaction/new-card/showNewCardResult';

export const createNewCard = (userTitle: string) => {
  post(userTitle)
    .then(response => showNewCardResult(response.status))
    .catch(() => alert('Error! New card was not created...'));
};
