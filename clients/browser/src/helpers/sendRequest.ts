export const sendRequest = (url: string, method?: string, body?: string) => {
  if ( !(method && body) ) {
    return fetch(url);
  }
  return fetch(url, {
    method: method,
    body: body,
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
  });
};
