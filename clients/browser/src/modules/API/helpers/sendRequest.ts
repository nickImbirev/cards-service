export const sendRequest = (url: string, method?: string, body?: object): Promise<Response> => {
  if ( !(method && body) ) {
    return fetch(url);
  }
  return fetch(url, {
    method: method,
    body: JSON.stringify(body),
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
  });
};
