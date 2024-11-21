const buildMjml = require("./buildMjml.js");
const copyAssets = require("./copyAssets.js");
const generateHtmlTemplate = require("./generateHtmlTemplates.js");

async function build() {
  await copyAssets();
  await buildMjml();
  await generateHtmlTemplate();
}

module.exports = build;