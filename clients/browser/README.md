# Cards Browser Client

## Getting started

### How to follow the source code standards

#### Code style

Please, install the [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode) VSCode extension to follow the same code style in the project.

Alternatively, there is a NPM script: `pretty` to prettify all the files in the folder recursively.

#### Linters

Please, install [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) VSCode extension to follow the same lint rules with the others and have VSCode support to help and raise errors during the compilation.

Alternatively, there is a NPM script: `lint` to check all the files in the folder recursively for the linting or compilation errors.

### How to debug the application locally

_*Warn: there is a requirement to have Google Chrome preinstalled on the development computer.*_

There is a debug [configuration](/.vscode/launch.json) already available to reuse locally.

1. Press F5 to start the debugging process and wait several seconds.
2. Make changes in the source code and see them immidiately propagated in the debug window in browser.

For more tips on how to debug the application in Chrome - please, follow this [guide](https://marketplace.visualstudio.com/items?itemName=msjsdiag.debugger-for-chrome).
