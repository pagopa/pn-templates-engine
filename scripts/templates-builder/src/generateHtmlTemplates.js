const path = require("node:path");
const ejs = require("ejs");
const fs = require("fs-extra");
const {
  BASE_SOURCE_DIR,
  loadTranslations,
  BASE_OUTPUT_DIR,
  getOutputFileName,
} = require("./utils");
const log = require("./logger");

const templatesDir = path.join(BASE_SOURCE_DIR, "templates");

async function generateHtmlTemplate() {
  log.info("...generating html templates");

  try {

    const entries = await fs.readdir(templatesDir, { withFileTypes: true });

    for (const entry of entries) {
      const templateName = entry.name;

      if (!entry.isDirectory() || entry.name === "partials") {
        continue;
      }

      const templateDir = path.join(templatesDir, templateName);

      if (!fs.pathExistsSync(path.join(templateDir, "index.html"))) {
        continue;
      }

      const i18nDir = path.join(templateDir, "i18n");

      const templateContent = await fs.readFile(
        path.join(templateDir, "index.html"),
        "utf8"
      );

      const hasLanguages = fs.existsSync(i18nDir);

      if (!hasLanguages) {
        const outputFileName = getOutputFileName(templateName);
        await renderAndWriteFile({
          templateName,
          fileName: outputFileName,
          content: templateContent,
        });
        continue;
      }

      const languages = (await fs.readdir(i18nDir)).map(
        (entry) => entry.split(".")[0]
      );

      for (const lang of languages) {
        const outputFileName = getOutputFileName(templateName, lang);

        // if it template exists, use it
        if (
          lang === "it" &&
          fs.existsSync(path.join(templateDir, "index_it.html"))
        ) {
          const langContent = await fs.readFile(
            path.join(templateDir, "index_it.html"),
            "utf8"
          );
          await renderAndWriteFile({
            templateName,
            fileName: outputFileName,
            content: langContent,
          });
          continue;
        }

        const translations = await loadTranslations(lang, templateDir);

        await renderAndWriteFile({
          templateName,
          fileName: outputFileName,
          content: templateContent,
          renderData: {
            ...translations,
            noIta: lang !== "it",
          },
        });
      }
    }

    log.info("Html templates generated!");
  } catch (err) {
    log.error(err);
    throw new Error("Error generating html templates");
  }
}

/**
 * Render the template and write the output file
 * @param {object} renderData: [OPTIONAL] data render ejs
 */
async function renderAndWriteFile({
  templateName,
  fileName,
  content,
  renderData,
}) {
  const outputFileName = fileName;

  const outputDir = path.join(BASE_OUTPUT_DIR, "templates", templateName);
  await fs.ensureDir(outputDir);

  const outputFile = path.join(outputDir, `${outputFileName}.html`);

  log.debug(`Writing ${outputFile}`);
  const renderedContent = ejs.render(content, renderData || {});
  await fs.writeFile(outputFile, renderedContent, "utf8");
}

module.exports = generateHtmlTemplate;
