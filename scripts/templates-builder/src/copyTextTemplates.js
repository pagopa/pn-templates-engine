const path = require("node:path");
const fs = require("fs-extra");
const { BASE_SOURCE_DIR, BASE_OUTPUT_DIR } = require("./utils");
const log = require("./logger");

const LANGUAGES = ["de", "fr", "sl"];
const templatesDir = path.join(BASE_SOURCE_DIR, "templates");

async function generateTextTemplates() {
  log.info("...copying text templates");

  try {
    const entries = await fs.readdir(templatesDir, { withFileTypes: true });

    for (const entry of entries) {
      const templateName = entry.name;

      if (!entry.isDirectory() || entry.name === "partials") {
        continue;
      }

      const templateDir = path.join(templatesDir, templateName);

      const isTextTemplate = fs.pathExistsSync(
        path.join(templateDir, "index.txt")
      );

      if (!isTextTemplate) {
        continue;
      }

      const outputFile = path.join(
        BASE_OUTPUT_DIR,
        "templates",
        templateName,
        `${templateName}.txt`
      );

      log.debug(`Writing ${outputFile}`);
      await fs.copy(path.join(templateDir, "index.txt"), outputFile);

      for (const lang of LANGUAGES) {
        if (fs.pathExistsSync(path.join(templateDir, `index_${lang}.txt`))) {
          const localizedOutputFile = path.join(
            BASE_OUTPUT_DIR,
            "templates",
            templateName,
            `${templateName}_${lang}.txt`
          );

          log.debug(`Writing ${localizedOutputFile}`);
          await fs.copy(
            path.join(templateDir, `index_${lang}.txt`),
            localizedOutputFile
          );
        }
      }
    }

    log.info("Text templates copied!");
  } catch (err) {
    log.error(err);
    throw new Error("Error copying text templates");
  }
}

module.exports = generateTextTemplates;
