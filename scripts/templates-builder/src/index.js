const build = require("./script");
const log = require("./logger");

build()
  .then(() => {
    log.info("Build completed successfully!");
  })
  .catch((err) => {
    log.error(err);
    process.exit(1);
  });
