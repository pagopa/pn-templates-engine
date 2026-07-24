const path = require("node:path");
const fs = require("fs-extra");

const BASE_SOURCE_DIR = path.join(__dirname, "../../../templates-assets");
const BASE_OUTPUT_DIR = path.join(
  __dirname,
  "../../../src/main/resources/generated-templates-assets"
);

// Cartella centralizzata delle traduzioni gestite da Lokalise:
// templates-assets/locales/<lang>/<Template>.json
const LOCALES_DIR = path.join(BASE_SOURCE_DIR, "locales");

async function loadTranslations(language, templateDir) {
  const lngFile = path.join(templateDir, "i18n", `${language}.json`);
  const itaFile = path.join(templateDir, "i18n", `it.json`);

  const lngContent = await fs.readJson(lngFile);
  const itaContent = await fs.readJson(itaFile);

  return { ...itaContent, ...lngContent };
}

// Se esiste locales/it/<Template>.json.
function hasLocales(templateName) {
  return fs.pathExistsSync(
    path.join(LOCALES_DIR, "it", `${templateName}.json`)
  );
}

// Lingue disponibili per un template nella cartella locales
function getLocalesLanguages(templateName) {
  return fs
    .readdirSync(LOCALES_DIR)
    .filter((lang) => {
      const langDir = path.join(LOCALES_DIR, lang);
      return (
        fs.statSync(langDir).isDirectory() &&
        fs.pathExistsSync(path.join(langDir, `${templateName}.json`))
      );
    });
}

/**
 * Carica le traduzioni per un template dalla cartella locales.
 * Le chiavi della lingua base (it) vengono ri-prefissate con `it_` per
 * mantenere l'italiano incorporato nei documenti in lingua (bilingui),
 * replicando la forma prodotta da loadTranslations (i18n legacy).
 */
async function loadLocalesTranslations(language, templateName) {
  const baseFile = path.join(LOCALES_DIR, "it", `${templateName}.json`);
  const lngFile = path.join(LOCALES_DIR, language, `${templateName}.json`);

  const baseContent = await fs.readJson(baseFile);
  const lngContent = await fs.readJson(lngFile);

  const basePrefixed = Object.fromEntries(
    Object.entries(baseContent).map(([key, value]) => [`it_${key}`, value])
  );

  return { ...basePrefixed, ...lngContent };
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
  LOCALES_DIR,
  loadTranslations,
  hasLocales,
  getLocalesLanguages,
  loadLocalesTranslations,
  getOutputFileName,
};
