const path = require("node:path");
const fs = require("node:fs").promises;
const util = require("node:util");
const { BASE_SOURCE_DIR } = require("./utils");
const log = require("./logger");
const exec = util.promisify(require("node:child_process").exec);

const templatesDir = path.join(BASE_SOURCE_DIR, "templates");

async function processMJMLFiles(dir) {
  try {
    const entries = await fs.readdir(dir, { withFileTypes: true });

    for (const entry of entries) {
      const fullPath = path.join(dir, entry.name);

      if (entry.isDirectory() && entry.name !== "partials") {
        await processMJMLFiles(fullPath);
        continue;
      }

      if (entry.isFile() && entry.name === "index.mjml") {
        const outputFile = path.join(dir, "index.html");

        log.debug(`Processing ${fullPath}`);
        await exec(`npx mjml ${fullPath} -o ${outputFile}`);
      }
    }
  } catch (err) {
    log.error("Error processing mjml");
    throw err;
  }
}

async function buildMjml() {
  log.info("...building email templates");

  await processMJMLFiles(templatesDir);

  log.info("Email templates built!");
}

module.exports = buildMjml;
