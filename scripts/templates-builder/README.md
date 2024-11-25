# HTML Templates Generator

## Requirements

- Node.js v20 or higher

## Generate Script

To generate the final templates in the output folder, run the command:

`npm run build`

For debug logs:

`npm run build -- --debug`

## Test

Tests are executed using Jest (<https://jestjs.io/>).

`npm run test`

The tests generate the templates in the `test/mock/output` folder starting from the test templates in the `test/mock/source` folder.
For a detailed test coverage report, open the file `test/coverage/lcov-report/index.html` in the browser.
