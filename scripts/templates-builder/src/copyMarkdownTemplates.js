const path = require("node:path");
const fs = require("fs-extra");
const { BASE_SOURCE_DIR, BASE_OUTPUT_DIR } = require("./utils");
const log = require("./logger");

const LANGUAGES = ["de", "fr", "sl", "en"];
const templatesDir = path.join(BASE_SOURCE_DIR, "templates");

async function copyMarkdownTemplates() {
  log.info("...copying markdown templates");

  try {
    const entries = await fs.readdir(templatesDir, { withFileTypes: true });

    for (const entry of entries) {
      const templateName = entry.name;

      if (!entry.isDirectory() || entry.name === "partials") {
        continue;
      }

      const templateDir = path.join(templatesDir, templateName);

      const isMarkdownTemplate = fs.pathExistsSync(
        path.join(templateDir, "index.md")
      );

      if (!isMarkdownTemplate) {
        continue;
      }

      const outputFile = path.join(
        BASE_OUTPUT_DIR,
        "templates",
        templateName,
        `${templateName}.md`
      );

      log.debug(`Writing ${outputFile}`);
      await fs.copy(path.join(templateDir, "index.md"), outputFile);

      for (const lang of LANGUAGES) {
        if (fs.pathExistsSync(path.join(templateDir, `index_${lang}.md`))) {
          const localizedOutputFile = path.join(
            BASE_OUTPUT_DIR,
            "templates",
            templateName,
            `${templateName}_${lang}.md`
          );

          log.debug(`Writing ${localizedOutputFile}`);
          await fs.copy(
            path.join(templateDir, `index_${lang}.md`),
            localizedOutputFile
          );
        }
      }
    }

    log.info("Markdown templates copied!");
  } catch (err) {
    log.error(err);
    throw new Error("Error copying markdown templates");
  }
}

module.exports = copyMarkdownTemplates;
