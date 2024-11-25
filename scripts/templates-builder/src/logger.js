const testing = process.env.NODE_ENV === "test";
const debug = process.argv.includes("--debug");

const logger = {
  error: (message) => {
    if (testing) {
      return;
    }
    console.error(`[ERROR] ${message}`);
  },
  info: (message) => {
    if (testing) {
      return;
    }
    console.info(`[INFO] ${message}`);
  },
  debug: (message) => {
    if (testing) {
      return;
    }
    if (debug) {
      console.debug(`[DEBUG] ${message}`);
    }
  },
};

module.exports = logger;
