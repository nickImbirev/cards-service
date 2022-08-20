export const showNewCardResult = (status: number): void => {
  if (status === 201) {
    alert('New card was created!');
  } else {
    alert('Error! New card was not created...');
  }
};
