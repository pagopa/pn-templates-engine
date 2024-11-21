const path = require("node:path");
const fs = require("fs-extra");

const BASE_SOURCE_DIR = path.join(__dirname, "../../../templates-assets");
const BASE_OUTPUT_DIR = path.join(
  __dirname,
  "../../../target/generated-templates-assets"
);

async function loadTranslations(language, templateDir) {
  const lngFile = path.join(templateDir, "i18n", `${language}.json`);
  const itaFile = path.join(templateDir, "i18n", `it.json`);

  const lngContent = await fs.readJson(lngFile);
  const itaContent = await fs.readJson(itaFile);

  return { ...itaContent, ...lngContent };
}

// TODO: discuss this solution (and template name) with the team
const RENAME_FOR_USER_ATTRIBUTES = {
  Mail_VerificationCode: "emailbody",
  PEC_VerificationCodeMessage: "pecbody",
  PEC_ValidationContactsSuccessMessage: "pecbodyconfirm",
  PEC_ValidationContactsRejectMessage: "pecbodyreject",
};

function getOutputTemplateName(templateName) {
  return RENAME_FOR_USER_ATTRIBUTES[templateName] || templateName;
}

function getOutputFileName(templateName, language) {
  const template = getOutputTemplateName(templateName);
  if (!language || language === "it") {
    return template;
  }
  return `${template}_${language}`;
}

module.exports = {
  BASE_SOURCE_DIR,
  BASE_OUTPUT_DIR,
  loadTranslations,
  getOutputTemplateName,
  getOutputFileName,
};
