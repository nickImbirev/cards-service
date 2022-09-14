export const createNode = (
  tagName: string,
  className: string,
  textContent?: string
): HTMLElement => {
  const node: HTMLElement = document.createElement(tagName);
  node.className = className;
  if (textContent) {
    node.textContent = textContent;
  }
  return node;
};
