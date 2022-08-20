import { sendRequest } from '../helpers/sendRequest';

const newCardURL = 'http://localhost:8081/card';

export const createNewCard = (userTitle: string): Promise<Response> => {
  return sendRequest(newCardURL, 'POST', { title: userTitle });
};
