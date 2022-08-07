export const createNode = (tagName: string, className: string,
  attributesNames?: [string], attributesValues?: [string]) => {
  const node = document.createElement(tagName);
  node.className = className;
  if (attributesNames && attributesValues) {
    attributesNames.forEach((attributeName, index) => {
      node.setAttribute(attributeName, attributesValues[index]);
    });
  }
  return node;
};
