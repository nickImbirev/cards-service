import { sendRequest } from '../helpers/sendRequest';

const todayCardsURL = 'http://localhost:8081/today/cards';

export const getTodayCards = (): Promise<Response> => {
  return sendRequest(todayCardsURL);
};
