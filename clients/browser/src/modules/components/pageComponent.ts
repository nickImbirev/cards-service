import { renderMainComponent, MainComponent } from './mainComponent';

export const renderPage = async (): Promise<void> => {
  const renderedMainComponent: Promise<MainComponent> = renderMainComponent();
  document.body.append(await renderedMainComponent);
};
