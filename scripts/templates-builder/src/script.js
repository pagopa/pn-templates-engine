const buildMjml = require("./buildMjml.js");
const copyAssets = require("./copyAssets.js");
const generateHtmlTemplate = require("./generateHtmlTemplates.js");
const copyTextTemplates = require("./copyTextTemplates.js");
const { BASE_OUTPUT_DIR } = require("./utils.js");
const fs = require("fs-extra");

async function build() {
  await fs.emptyDir(BASE_OUTPUT_DIR);

  await copyAssets();
  await buildMjml();
  await copyTextTemplates();
  await generateHtmlTemplate();
}

module.exports = build;
