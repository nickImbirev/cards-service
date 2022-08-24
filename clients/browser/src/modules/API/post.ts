export const post = (userTitle: string) => {
  return fetch('http://localhost:8081/card', {
    method: 'POST',
    body: JSON.stringify({ title: userTitle }),
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
  });
};
