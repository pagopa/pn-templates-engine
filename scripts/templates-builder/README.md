# Html templates generator

## Requirements

- Node.js v20 or higher

## Generate script

Per generare i template finali nella cartella output, lanciare il comando:

`npm run build`

Per avere log di debug:

`npm run build -- --debug`

## Test

I test sono eseguiti utilizzando Jest (<https://jestjs.io/>).

`npm run test`

I test generano i template nella cartella `test/mock/output` partendo dai template di test della cartella `test/mock/source`.
Per un rapporto dettagliato del test coverage, aprire nel browser il file `test/coverage/lcov-report/index.html`.
