const pino = require("pino");

const logger = pino({
  base: undefined,
  transport: { target: "pino-pretty" },
  level: (() => {
    if (process.env.NODE_ENV === "test") {
      return "silent";
    }
    if (process.argv.includes("--debug")) {
      return "debug";
    }
    return "info";
  })(),
});

module.exports = logger;
