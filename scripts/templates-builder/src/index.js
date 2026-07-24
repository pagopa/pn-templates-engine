const build = require("./script");
const log = require("./logger");

const useLocales = process.argv.slice(2).includes("locales");

build({ useLocales })
  .then(() => {
    log.info("Build completed successfully!");
  })
  .catch((err) => {
    log.error(err);
    process.exit(1);
  });
