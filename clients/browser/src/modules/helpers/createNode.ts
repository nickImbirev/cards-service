export const createNode = (
  tagName: string,
  className: string,
  textContent?: string,
  event?: string,
  callback?: VoidFunction
): HTMLElement => {
  const node: HTMLElement = document.createElement(tagName);
  node.className = className;

  if (textContent) node.textContent = textContent;
  if (event && callback) node.addEventListener(event, callback);

  return node;
};
