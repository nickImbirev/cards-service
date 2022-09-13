export const createNode = (
  tagName: string,
  className: string,
  textContent?: string,
  attributesNames?: [string],
  attributesValues?: [string]
): HTMLElement => {
  const node: HTMLElement = document.createElement(tagName);
  node.className = className;
  if (textContent) {
    node.textContent = textContent;
  }
  if (attributesNames && attributesValues) {
    attributesNames.forEach((attributeName, index) => {
      node.setAttribute(attributeName, attributesValues[index]);
    });
  }
  return node;
};
