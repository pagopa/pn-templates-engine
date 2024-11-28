const path = require("node:path");
const fs = require("fs-extra");

const BASE_SOURCE_DIR = path.join(__dirname, "../../../templates-assets");
const BASE_OUTPUT_DIR = path.join(
  __dirname,
  "../../../src/main/resources/generated-templates-assets"
);

async function loadTranslations(language, templateDir) {
  const lngFile = path.join(templateDir, "i18n", `${language}.json`);
  const itaFile = path.join(templateDir, "i18n", `it.json`);

  const lngContent = await fs.readJson(lngFile);
  const itaContent = await fs.readJson(itaFile);

  return { ...itaContent, ...lngContent };
}

function getOutputFileName(templateName, language) {
  if (!language || language === "it") {
    return templateName;
  }
  return `${templateName}_${language}`;
}

module.exports = {
  BASE_SOURCE_DIR,
  BASE_OUTPUT_DIR,
  loadTranslations,
  getOutputFileName,
};
